package com.wondersgroup.healthcloud.services.remind.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.wondersgroup.common.http.builder.RequestBuilder;
import com.wondersgroup.common.http.entity.JsonNodeResponseWrapper;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.exceptions.Exceptions;
import com.wondersgroup.healthcloud.helper.push.api.PushClientWrapper;
import com.wondersgroup.healthcloud.helper.push.area.PushAreaService;
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
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.*;

/**
 * Created by Admin on 2017/4/11.
 */
@Service
public class RemindServiceImpl implements RemindService {

    protected static final Logger logger = LoggerFactory.getLogger(RemindServiceImpl.class);

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Value("${JOB_CONNECTION_URL}")
    private String jobClientUrl;

    private HttpRequestExecutorManager httpRequestExecutorManager = new HttpRequestExecutorManager(new OkHttpClient());

    @Autowired
    private RemindRepository remindRepo;

    @Autowired
    private RemindItemRepository remindItemRepo;

    @Autowired
    private RemindTimeRepository remindTimeRepo;

    @Autowired
    private CommonlyUsedMedicineRepository commonlyUsedMedicineRepo;

    @Autowired
    private PushClientWrapper pushClientWrapper;

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
        int flag = -1;// 返回标志,0-成功,-1-失败
        try {
            HashMap<String, CommonlyUsedMedicine> cumMap = new HashMap<>();
            if (StringUtils.isNotEmpty(remind.getUserId())) {
                // 获取用户常用药品列表
                List<CommonlyUsedMedicine> cums = commonlyUsedMedicineRepo.findByUserId(remind.getUserId(), remind.getType());
                if (cums != null && cums.size() > 0) {
                    for (CommonlyUsedMedicine cum : cums) {
                        cumMap.put(cum.getMedicineId(), cum);
                    }
                }
            } else {
                return flag;
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
                if (cum != null && StringUtils.isNotEmpty(cum.getId())) {
                    cum.setName(ri.getName());
                    cum.setSpecification(ri.getSpecification());
                    cum.setDose(ri.getDose());
                    cum.setUnit(ri.getUnit());
                    cum.setUpdateTime(now);
                    saveCUMs.add(cum);
                } else {
                    CommonlyUsedMedicine tmpCUM = new CommonlyUsedMedicine(ri);
                    tmpCUM.setId(IdGen.uuid());
                    tmpCUM.setUserId(remind.getUserId());
                    tmpCUM.setType(remind.getType());
                    tmpCUM.setDelFlag("0");
                    tmpCUM.setCreateTime(now);
                    tmpCUM.setUpdateTime(now);
                    saveCUMs.add(tmpCUM);
                }
            }
            remindItemRepo.save(Arrays.asList(remindItems));// 保存药品信息
            remindTimeRepo.save(Arrays.asList(remindTimes));// 保存时间信息
            remindRepo.save(remind);// 保存用药提醒
            commonlyUsedMedicineRepo.save(saveCUMs);// 保存常用药品
            try {
                // 生成定时提醒任务
                if (remindTimes != null && remindTimes.length > 0) {
                    DateTime nowDateTime = new DateTime(now);
                    String datePrefix = nowDateTime.toString(DATE_FORMAT);

                    StringBuffer strBufRTs = new StringBuffer();
                    for (RemindTime rt : remindTimes) {
                        DateTime remindTime = DateTimeFormat.forPattern(DATE_TIME_FORMAT).parseDateTime(datePrefix + " " + rt.getRemindTime().toString());
                        if (remindTime.isBefore(nowDateTime)) {// 提醒时间早于等于当前时间
                            remindTime.plusDays(1);
                        }
                        if (strBufRTs.length() == 0) {
                            strBufRTs.append(remindTime.toString(DATE_TIME_FORMAT)).append("#").append(rt.getId());
                        } else {
                            strBufRTs.append(",").append(remindTime.toString(DATE_TIME_FORMAT)).append("#").append(rt.getId());
                        }
                    }
                    Boolean rtnBoolean = generateJob(remind.getId(), strBufRTs.toString());
                }
            } catch (Exception ex) {
                logger.error(Exceptions.getStackTraceAsString(ex));
            }
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

    @Override
    public int medicationReminder(String id, String remindTimeId, String internalUrl) {

        int flag = -1;

        Remind remind = remindRepo.findOne(id);
        RemindTime rt = remindTimeRepo.findOne(remindTimeId);

        if (remind == null || "1".equals(remind.getDelFlag()) || rt == null) {
            // 若用药提醒被删除或禁用，以及提醒时间被删除，则不再提醒
            return 0;
        }

        // 发送用药提醒PUSH
        push(remind.getUserId(), "用药时间到，祝您身体健康", "天天笑哈哈", internalUrl);

        // 生成下次用药提醒任务
        try {
            Date now = new Date();

            DateTime nowDateTime = new DateTime(now);
            String datePrefix = nowDateTime.toString(DATE_FORMAT);

            DateTime remindTime = new DateTime(datePrefix + " " + rt.getRemindTime().toString());

            Boolean rtnBoolean = generateJob(remind.getId(), remind.getId() + "#" + remindTime.plusDays(1).toString(DATE_TIME_FORMAT));
            if (rtnBoolean) {
                flag = 0;
            }
        } catch (Exception ex) {
            logger.error(Exceptions.getStackTraceAsString(ex));
        }
        return flag;
    }

    private Boolean push(String userId, String title, String content, String internalUrl) {

        boolean result = false;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("content", content);
        map.put("type", "SYSTEM");
        map.put("persistence", false);
        map.put("area_special", false);
        map.put("is_doctor", false);
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonObj = mapper.writeValueAsString(map);
            HttpEntity<String> formEntity = new HttpEntity<String>(jsonObj, headers);

            String resultStr = restTemplate.postForObject(internalUrl + "message/push/single?alias=" + userId, formEntity,
                    String.class);
            result = true;
        } catch (JsonProcessingException e) {
            logger.error(Exceptions.getStackTraceAsString(e));
        }
        return result;
    }

    private Boolean generateJob(String remindId, String params) {
        try {
            //调用jobClient的接口
            Map<String, String> param = new HashMap<>();
            param.put("id", remindId);
            param.put("timeAndIds", params);
            Request req = new RequestBuilder().get().url(jobClientUrl + "/api/jobclient/remind/medicationReminder").params(param).build();
            JsonNodeResponseWrapper response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(req).run().as(JsonNodeResponseWrapper.class);
        } catch (Exception e) {
            logger.error(Exceptions.getStackTraceAsString(e));
            return false;
        }
        return true;
    }
}
