package com.wondersgroup.healthcloud.api.http.controllers.doctormessage;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.doctormessage.DoctorMessage;
import com.wondersgroup.healthcloud.services.doctormessage.ManageDoctorMessageService;
import com.wondersgroup.healthcloud.utils.DateFormatter;

/**
 * Created by qiujun on 2015/9/10.
 */
@RestController
@RequestMapping("/api/doctorMessage")
public class DoctorMessageController {

    private static Logger log = LoggerFactory.getLogger("EX");
    private static final int PAGE_SIZE = 20;

    @Resource
    private ManageDoctorMessageService manageDoctorMessageService;

    @RequestMapping(value = "/getMessages", method = RequestMethod.GET)
    @VersionRange
    public JsonListResponseEntity<DoctorMessage> getDoctorMessage(@RequestParam String uid,
                                                                  @RequestParam String type,
                                                                  @RequestParam(required = false) Integer flag){

        JsonListResponseEntity<DoctorMessage> entity = new JsonListResponseEntity<>();
        try{
            Integer position = 0;
            if(flag!=null){
                position = flag;
            }

            List<DoctorMessage> doctorMessages = manageDoctorMessageService.queryDoctorMessageByTpye(type, uid);

            if(doctorMessages!=null&&!doctorMessages.isEmpty()){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for(DoctorMessage dm : doctorMessages) {
                    if(dm.getUpdateDate() != null  && dm.getUpdateDate().indexOf(".") > 0) {
                        dm.setUpdateDate(dm.getUpdateDate().substring(0, dm.getUpdateDate().indexOf(".")));
                    }
                }
                entity = new Page().handleResponseEntity(entity, position, (position + PAGE_SIZE),
                        PAGE_SIZE, doctorMessages);
            }else{
                entity.setContent((List) Collections.emptyList());
            }

        }catch(Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            entity.setCode(3010);
            entity.setMsg("调用失败");
        }
        return entity;
    }

    @RequestMapping(value = "/writeMessage", method = RequestMethod.POST)
    @VersionRange
    public JsonResponseEntity<String> writeMessage(@RequestBody String request){

        JsonResponseEntity<String> body = new JsonResponseEntity<>();
        try{
            JsonKeyReader reader = new JsonKeyReader(request);
            DoctorMessage message = generateMessage(reader);
            manageDoctorMessageService.addDoctorMessage(message);
            body.setMsg("发送成功");
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            body.setMsg("发送失败");
            body.setCode(3010);
        }

        return body;
    }

    /**
     * 医生首页tab消息红点提示 4.1版本
     * @param uid
     * @return
     */
    @GetMapping(path = "/prompt")
    @VersionRange
    public JsonResponseEntity<Map<String, Object>> prompt(@RequestParam String uid) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("has_unread", true);
        return new JsonResponseEntity<>(0, null, map);
    }


    private DoctorMessage generateMessage(JsonKeyReader reader) {

        String send = reader.readString("send",true);
        String sendId = reader.readString("sendId",true);
        String receive = reader.readString("receive",true);
        String receiveId = reader.readString("receiveId",true);
        String title = reader.readString("title",true);
        String content = reader.readString("content",true);
        DoctorMessage message = new DoctorMessage();
        message.setId(UUID.randomUUID().toString().replace("-",""));
        message.setTitle(title);
        message.setContent(content);
        message.setReceive(receive);
        message.setReceiveId(receiveId);
        message.setSend(send);
        message.setSendId(sendId);
        message.setMsgType("2");//私信
        message.setUpdateDate(DateFormatter.dateTimeFormat(new Date()));
        return message;
    }

    private class Page {

        public boolean more;

        public List pageObject(List origin, int fromIndex, int toIndex) {
            if (origin == null) {
                return null;
            }
            if (toIndex >= origin.size())
                toIndex = origin.size();

            return new ArrayList<>(origin.subList(fromIndex, toIndex));
        }

        public JsonListResponseEntity handleResponseEntity(JsonListResponseEntity body,
                                                           int fromIndex, int toIndex, int pageSize, List cases) {
            if (body == null)
                return null;
            List temp = pageObject(cases, fromIndex, toIndex);
            if (cases.size() > pageSize && fromIndex < cases.size() && temp.size() == PAGE_SIZE) {
                body.setContent(temp, true, "updateTime", String.valueOf(fromIndex + temp.size()));
            } else {
                body.setContent(temp, false, "updateTime", String.valueOf(cases.size()));
            }
            return body;
        }
    }
    }
