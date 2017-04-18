package com.wondersgroup.healthcloud.services.remind.impl;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.exceptions.Exceptions;
import com.wondersgroup.healthcloud.jpa.entity.medicine.CommonlyUsedMedicine;
import com.wondersgroup.healthcloud.jpa.entity.remind.Remind;
import com.wondersgroup.healthcloud.jpa.entity.remind.RemindItem;
import com.wondersgroup.healthcloud.jpa.entity.remind.RemindTime;
import com.wondersgroup.healthcloud.jpa.repository.remind.CommonlyUsedMedicineRepository;
import com.wondersgroup.healthcloud.jpa.repository.remind.RemindItemRepository;
import com.wondersgroup.healthcloud.jpa.repository.remind.RemindRepository;
import com.wondersgroup.healthcloud.jpa.repository.remind.RemindTimeRepository;
import com.wondersgroup.healthcloud.services.remind.RemindService;
import com.wondersgroup.healthcloud.services.remind.dto.RemindDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

/**
 * Created by Admin on 2017/4/11.
 */
@Service
public class RemindServiceImpl implements RemindService {

    protected static final Logger logger = LoggerFactory.getLogger(RemindServiceImpl.class);

    @Autowired
    private RemindRepository remindRepo;

    @Autowired
    private RemindItemRepository remindItemRepo;

    @Autowired
    private RemindTimeRepository remindTimeRepo;

    @Autowired
    private CommonlyUsedMedicineRepository commonlyUsedMedicineRepo;

    @Override
    public List<RemindDTO> list(String userId, int pageNo, int pageSize) {
        List<RemindDTO> remindDTOS = new ArrayList<>();
        List<Remind> reminds = remindRepo.findByUserId(userId, pageNo * (pageSize - 1), pageSize);
        if (reminds != null && reminds.size() > 0) {
            for (Remind remind : reminds) {
                List<RemindItem> remindItems = remindItemRepo.findByRemindId(remind.getId());
                List<RemindTime> remindTimes = remindTimeRepo.findByRemindId(remind.getId());
                remindDTOS.add(new RemindDTO(remind, remindItems, remindTimes));
            }
        }
        return remindDTOS;
    }

    @Override
    public RemindDTO detail(String id) {
        try {
            Remind remind = remindRepo.findOne(id);
            if (remind != null) {
                List<RemindItem> remindItems = remindItemRepo.findByRemindId(id);
                List<RemindTime> remindTimes = remindTimeRepo.findByRemindId(id);
                return new RemindDTO(remind, remindItems, remindTimes);
            }
        } catch (Exception ex) {
            logger.error(Exceptions.getStackTraceAsString(ex));
        }
        return null;
    }

    @Transactional
    @Override
    public int saveAndUpdate(Remind remind, RemindItem[] remindItems, RemindTime[] remindTimes, RemindItem[] delRemindItems, RemindTime[] delRemindTimes) {
        try {
            HashMap<String, CommonlyUsedMedicine> cumMap = new HashMap<>();
            if (StringUtils.isNotEmpty(remind.getUserId())) {
                // 获取用户常用药品列表
                List<CommonlyUsedMedicine> cums = commonlyUsedMedicineRepo.findByUserId(remind.getUserId());
                if (cums != null && cums.size() > 0) {
                    for (CommonlyUsedMedicine cum : cums) {
                        cumMap.put(cum.getMedicineId(), cum);
                    }
                }
            } else {
                return -1;
            }

            // 批量删除药品
            if (delRemindItems != null && delRemindItems.length > 0) {
                remindItemRepo.deleteInBatch(Arrays.asList(delRemindItems));
            }

            // 批量删除时间
            if (delRemindTimes != null && delRemindTimes.length > 0) {
                remindTimeRepo.deleteInBatch(Arrays.asList(delRemindTimes));
            }

            Date now = new Date();
            if (StringUtils.isEmpty(remind.getId())) {// 新建提醒
                String id = IdGen.uuid();
                remind.setId(id);
                remind.setDelFlag("0");
                remind.setCreateTime(now);
                remind.setUpdateTime(now);

                for (RemindItem remindItem : remindItems) {
                    remindItem.setId(IdGen.uuid());
                    remindItem.setRemindId(id);
                    remindItem.setDelFlag("0");
                    remindItem.setCreateTime(now);
                    remindItem.setUpdateTime(now);
                }
                for (RemindTime remindTime : remindTimes) {
                    remindTime.setId(IdGen.uuid());
                    remindTime.setRemindId(id);
                    remindTime.setDelFlag("0");
                    remindTime.setCreateTime(now);
                    remindTime.setUpdateTime(now);
                }
            } else {// 更新提醒
                remind.setUpdateTime(now);
                for (RemindItem remindItem : remindItems) {
                    if (StringUtils.isEmpty(remindItem.getId())) {
                        remindItem.setId(IdGen.uuid());
                        remindItem.setRemindId(remind.getId());
                        remindItem.setDelFlag("0");
                        remindItem.setCreateTime(now);
                    }
                    remindItem.setUpdateTime(now);
                }
                for (RemindTime remindTime : remindTimes) {
                    if (StringUtils.isEmpty(remindTime.getId())) {
                        remindTime.setId(IdGen.uuid());
                        remindTime.setRemindId(remind.getId());
                        remindTime.setDelFlag("0");
                        remindTime.setCreateTime(now);
                    }
                    remindTime.setUpdateTime(now);
                }
            }

            List<CommonlyUsedMedicine> saveCUMs = new ArrayList<>();
            for (RemindItem ri : remindItems) {
                CommonlyUsedMedicine cum = cumMap.get(ri.getMedicineId());
                CommonlyUsedMedicine tmpCUM = new CommonlyUsedMedicine(ri);
                if (cum != null && StringUtils.isNotEmpty(cum.getId())) {
                    tmpCUM.setUpdateTime(now);
                } else {
                    tmpCUM.setId(IdGen.uuid());
                    tmpCUM.setUserId(remind.getUserId());
                    tmpCUM.setDelFlag("0");
                    tmpCUM.setCreateTime(now);
                    tmpCUM.setUpdateTime(now);
                }
                saveCUMs.add(tmpCUM);
            }
            remindItemRepo.save(Arrays.asList(remindItems));// 保存药品信息
            remindTimeRepo.save(Arrays.asList(remindTimes));// 保存时间信息
            remindRepo.save(remind);// 保存用药提醒
            commonlyUsedMedicineRepo.save(saveCUMs);// 保存常用药品
            return 0;
        } catch (Exception ex) {
            logger.error(Exceptions.getStackTraceAsString(ex));
        }
        return -1;
    }

    @Transactional
    @Override
    public int enableOrDisableRemind(String id) {
        try {
            Remind remind = remindRepo.findOne(id);
            if (remind != null) {
                if ("0".equals(remind.getDelFlag())) {
                    remind.setDelFlag("1");
                } else {
                    remind.setDelFlag("0");
                }
                remind.setUpdateTime(new Date());
                remindRepo.save(remind);
                return 0;
            }
        } catch (Exception ex) {
            logger.error(Exceptions.getStackTraceAsString(ex));
        }
        return -1;
    }

    @Transactional
    @Override
    public int deleteRemind(String id) {
        try {
            List<RemindItem> remindItems = remindItemRepo.findByRemindId(id);
            if (remindItems != null && remindItems.size() > 0) {
                remindItemRepo.deleteInBatch(remindItems);
            }

            List<RemindTime> remindTimes = remindTimeRepo.findByRemindId(id);
            if (remindTimes != null && remindTimes.size() > 0) {
                remindTimeRepo.deleteInBatch(remindTimes);
            }
            remindRepo.delete(id);
            return 0;
        } catch (Exception ex) {
            logger.error(Exceptions.getStackTraceAsString(ex));
        }
        return -1;
    }
}
