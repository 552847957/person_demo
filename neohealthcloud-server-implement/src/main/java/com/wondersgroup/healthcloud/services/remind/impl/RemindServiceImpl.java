package com.wondersgroup.healthcloud.services.remind.impl;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.wondersgroup.common.http.builder.RequestBuilder;
import com.wondersgroup.common.http.entity.JsonNodeResponseWrapper;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.exceptions.Exceptions;
import com.wondersgroup.healthcloud.helper.push.api.AppMessage;
import com.wondersgroup.healthcloud.helper.push.area.PushAreaService;
import com.wondersgroup.healthcloud.helper.push.getui.PushClient;
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
import com.wondersgroup.healthcloud.services.remind.dto.RemindForHomeDTO;
import com.wondersgroup.healthcloud.services.user.message.UserPrivateMessageService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    
    @Value("${disease.h5.url}")
    public String API_DISEASE_H5_URL;

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
    private UserPrivateMessageService userPrivateMessageService;

    @Autowired
    private PushAreaService pushAreaService;

    private RemindForHomeDTO remindForHomeDTO;

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
    public RemindForHomeDTO getRemindForHome(String userId){
        List<RemindTime> remindTimes = remindTimeRepo.findRemindByUid(userId);
        if(CollectionUtils.isEmpty(remindTimes)){
            RemindForHomeDTO  remindForHomeDTO=new RemindForHomeDTO();
            remindForHomeDTO.setName("您身体棒棒的暂无用药提醒");
            remindForHomeDTO.setTarGetUrl(API_DISEASE_H5_URL+"/MedicationReminderDrug/null");
            return remindForHomeDTO;
        }
        RemindForHomeDTO dto = new RemindForHomeDTO();
        long nowTime = 0;
        if(CollectionUtils.isNotEmpty(remindTimes)){
            if(remindTimes.size()==1){
                dto.setRemindTime(remindTimes.get(0).getRemindTime()+"");
                dto.setId(remindTimes.get(0).getId());
                List<RemindItem> remindItems = remindItemRepo.findByRemindId(remindTimes.get(0).getRemindId());
                dto.setName(getRemindMedicineName(remindItems));
            }else{
                nowTime = RemindForHomeDTO.stringToDate(RemindForHomeDTO.dateToString(new Date())).getTime();
                if(remindTimes.get(0).getRemindTime().getTime()<nowTime){
                    List<RemindItem> remindItems = remindItemRepo.findByRemindId(remindTimes.get(remindTimes.size()-1).getRemindId());
                    dto.setId(remindTimes.get(remindTimes.size()-1).getId());
                    dto.setRemindTime(RemindForHomeDTO.dateToString(remindTimes.get(remindTimes.size()-1).getRemindTime()));
                    dto.setName(getRemindMedicineName(remindItems));
                }else if(remindTimes.get(remindTimes.size()-1).getRemindTime().getTime()>nowTime){
                    List<RemindItem> remindItems = remindItemRepo.findByRemindId(remindTimes.get(remindTimes.size()-1).getRemindId());
                    dto.setId(remindTimes.get(remindTimes.size()-1).getId());
                    dto.setRemindTime(RemindForHomeDTO.dateToString(remindTimes.get(remindTimes.size()-1).getRemindTime()));
                    dto.setName(getRemindMedicineName(remindItems));
                }else{
                    for (int i = remindTimes.size()-1; i >=0 ; i--) {
                        long time = remindTimes.get(i).getRemindTime().getTime();
                        if(nowTime>time){
                            continue;
                        }
                        if(time>nowTime){
                            List<RemindItem> remindItems =  remindItemRepo.findByRemindId(remindTimes.get(i).getRemindId());
                            dto.setId(remindTimes.get(i).getId());
                            dto.setRemindTime(RemindForHomeDTO.dateToString(remindTimes.get(i).getRemindTime()));
                            dto.setName(getRemindMedicineName(remindItems));
                            break;
                        }
                    }
                }
            }
        }
        dto.setTarGetUrl(API_DISEASE_H5_URL+"/MedicationReminder");
//        for (RemindTime remind : reminds) {
//            List<RemindItem> remindItems = remindItemRepo.findByRemindId(remind.getId());
//            List<RemindTime> remindTimes = remindTimeRepo.findByRemindId(remind.getId());
//            //remindForHomeDTO=new RemindForHomeDTO(remind, remindItems, remindTimes);
//            remindForHomeDTO.setTarGetUrl(API_DISEASE_H5_URL+"/MedicationReminder");
//        }
        return dto;
    }
    
    public static String getRemindMedicineName(List<RemindItem> remindItems){
        String medicineName="";
        if(CollectionUtils.isNotEmpty(remindItems)){
            String name=remindItems.get(0).getName();
            if(name.length()>5){
                medicineName=name.substring(0, 5)+"...";
            }else{
                medicineName=name;
            }
        }
        return medicineName;
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
                    // 手工输入药品
                    if ("-".equals(remindItem.getMedicineId()) || StringUtils.isEmpty(remindItem.getMedicineId())) {
                        remindItem.setMedicineId(IdGen.uuid());
                    }
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
                    // 手工输入药品
                    if ("-".equals(remindItem.getMedicineId()) || StringUtils.isEmpty(remindItem.getMedicineId())) {
                        remindItem.setMedicineId(IdGen.uuid());
                    }
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
                    cum.setBrand(ri.getBrand());
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
                            remindTime = remindTime.plusDays(1);
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
    public int medicationReminder(String id, String remindTimeId) {

        int flag = -1;

        Remind remind = remindRepo.findOne(id);
        RemindTime rt = remindTimeRepo.findOne(remindTimeId);

        if (remind == null || "1".equals(remind.getDelFlag()) || rt == null) {
            // 若用药提醒被删除或禁用，以及提醒时间被删除，则不再提醒
            return 0;
        }

        List<RemindItem> ris = remindItemRepo.findByRemindId(id);
        if (ris == null || ris.size() < 1) {
            return 0;
        }
        // 目前用药提醒仅有一个药品
        RemindItem ri = ris.get(0);

        // 发送用药提醒PUSH
        push(remind.getUserId(), "用药提醒", "请您于" + rt.getRemindTime().toString().substring(0, 5) + "按时服用" + ("M".equals(remind.getType()) ? ri.getBrand() : ri.getName()));

        // 生成下次用药提醒任务
        try {

            DateTime nowDateTime = DateTime.now();
            String datePrefix = nowDateTime.toString(DATE_FORMAT);

            DateTimeFormatter format = DateTimeFormat.forPattern(DATE_TIME_FORMAT);
            DateTime remindTime = DateTime.parse(datePrefix + " " + rt.getRemindTime().toString(), format);

            //Boolean rtnBoolean = generateJob(remind.getId(), remind.getId() + "#" + remindTime.plusDays(1).toString(DATE_TIME_FORMAT));
            Boolean rtnBoolean = generateJob(remind.getId(), remindTime.plusMinutes(1).toString(DATE_TIME_FORMAT) + "#" + remind.getId());
            if (rtnBoolean) {
                flag = 0;
            }
        } catch (Exception ex) {
            logger.error(Exceptions.getStackTraceAsString(ex));
        }
        return flag;
    }

    private Boolean push(String userId, String title, String content) {
        try {
            AppMessage pushMessage = new AppMessage();
            pushMessage.title = title;
            pushMessage.content = content;
            pushMessage.persistence = false;// 不存入消息表

            userPrivateMessageService.saveOneMessage(pushMessage, userId);
            PushClient client = pushAreaService.getByUser(userId);
            if (client != null) {
                pushMessage.area = client.identityName();
            } else {
                return false;
            }
            client.pushToAliasWithExpireTime(pushMessage.toPushMessage(), userId, 15 * 60 * 1000);
            return true;
        } catch (Exception ex) {
            logger.error(Exceptions.getStackTraceAsString(ex));
        }
        return false;
    }

    private Boolean generateJob(String remindId, String params) {
        logger.info("生成用药提醒定时任务：remindId = " + remindId + " params = " + params);
        try {
            //调用jobClient的接口
            Map<String, String> param = new HashMap<>();
            param.put("id", remindId);
            param.put("timeAndIds", params);
            Request req = new RequestBuilder().get().url(jobClientUrl + "/api/jobclient/remind/medicationReminder").params(param).build();
            JsonNodeResponseWrapper response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(req).run().as(JsonNodeResponseWrapper.class);
            logger.info("JobClient response:" + response.body());
        } catch (Exception e) {
            logger.error(Exceptions.getStackTraceAsString(e));
            return false;
        }
        return true;
    }
}
