package com.wondersgroup.healthcloud.api.http.controllers.doctormessage;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wondersgroup.healthcloud.api.http.dto.doctor.message.DoctorMessageDTO;
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
    public JsonResponseEntity<Map<String, Object>> prompt(@RequestParam(required = true) String uid) {
        Map<String, Object> map = Maps.newHashMap();

        int count = manageDoctorMessageService.countUnreadMsgByUid(uid);
        Boolean hasUnread = false;
        if(count>0)
            hasUnread = true;

        map.put("has_unread", hasUnread);
        return new JsonResponseEntity<>(0, null, map);
    }

    /**
     * 消息页面 消息类型列表 4.1版本
     * @param uid
     * @return
     */
    @GetMapping(path = "/typeMsgList")
    @VersionRange
    public JsonListResponseEntity<DoctorMessageDTO> typeMsgList(@RequestParam(required = true) String uid) {
        JsonListResponseEntity<DoctorMessageDTO> response = new JsonListResponseEntity<>();
        List<DoctorMessageDTO> resultList = Lists.newArrayList();
        List<DoctorMessage> typeMsgList = manageDoctorMessageService.findTypeMsgListByUid(uid);
        for (DoctorMessage doctorMessage : typeMsgList){
            DoctorMessageDTO doctorMessageDTO = new DoctorMessageDTO(doctorMessage);
            int count = manageDoctorMessageService.countUnreadMsgByUidAndType(uid,doctorMessage.getMsgType());
            if(count>0){
                doctorMessageDTO.setHasUnread(true);
            }else{
                doctorMessageDTO.setHasUnread(false);
            }
            resultList.add(doctorMessageDTO);
        }
        response.setContent(resultList, false, null, null);
        return response;
    }

    /**
     * 分类查询消息列表
     * @param uid
     * @param msg_type
     * @return
     */
    @GetMapping(path = "/msgList")
    @VersionRange
    public JsonListResponseEntity<DoctorMessageDTO> msgList(@RequestParam(required = true) String uid,
                                                            @RequestParam(required = true) String msg_type,
                                                            @RequestParam(defaultValue = "0") String flag) {
        int pageSize = 20;
        JsonListResponseEntity<DoctorMessageDTO> response = new JsonListResponseEntity<>();
        List<DoctorMessageDTO> resultList = Lists.newArrayList();
        List<DoctorMessage> typeMsgList = manageDoctorMessageService.findMsgListByUidAndType(uid,msg_type,Integer.valueOf(flag),pageSize);
        for (DoctorMessage doctorMessage : typeMsgList){
            DoctorMessageDTO doctorMessageDTO = new DoctorMessageDTO(doctorMessage);
            doctorMessageDTO.setHasUnread(false);
            resultList.add(doctorMessageDTO);
        }
        //把医生下面所有这个类型的消息都设置为已读
        manageDoctorMessageService.setMsgIsReadByMsgType(uid,msg_type);

        response.setContent(resultList, false, null, null);
        return response;
    }

    /**
     * 单条消息删除
     * @param id
     * @return
     */
    @DeleteMapping(path = "/delete")
    @VersionRange
    public JsonResponseEntity<String> delateMsg(@RequestParam(required = true) String id) {
        JsonResponseEntity<String> response = new JsonResponseEntity<>();
        manageDoctorMessageService.deleteDoctorMsgById(id);
        response.setMsg("删除成功");
        return response;
    }

    /**
     * 根据msgType 删除类型所有消息
     * @param
     * @return
     */
    @DeleteMapping(path = "/msgTypeDelete")
    @VersionRange
    public JsonResponseEntity<String> delateMsgByType(@RequestParam(required = true) String uid,
                                                      @RequestParam(required = true) String msg_type) {
        JsonResponseEntity<String> response = new JsonResponseEntity<>();
        manageDoctorMessageService.deleteDoctorMsgByMsgType(uid,msg_type);
        response.setMsg("删除成功");
        return response;
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
