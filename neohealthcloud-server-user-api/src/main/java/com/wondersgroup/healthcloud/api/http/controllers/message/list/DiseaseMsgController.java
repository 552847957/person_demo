package com.wondersgroup.healthcloud.api.http.controllers.message.list;

import com.wondersgroup.healthcloud.api.utils.RequestDataReader;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.services.user.message.MsgService;
import com.wondersgroup.healthcloud.services.user.message.enums.MsgTypeEnum;
import com.wondersgroup.healthcloud.services.user.message.exception.EnumMatchException;
import com.wondersgroup.healthcloud.utils.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 消息列表-慢病消息接口
 * Created by jialing.yao on 2016-12-12.
 */
@RestController
@RequestMapping("/api/disease/message")
public class DiseaseMsgController {
    @Autowired
    private MsgService diseaseMsgService;

    /**
     * 慢病消息列表查询
     */
    @VersionRange
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Object getList(@RequestParam Map<String,Object> input){
        RequestDataReader reader = new RequestDataReader(input);
        String uid=reader.readString("uid",false);
        int pageNo=Integer.parseInt(reader.readDefaultString("flag","1"));
        int pageSize=Integer.parseInt(reader.readDefaultString("pageSize", "10"));

        Page page=new Page(pageNo,pageSize);
        Page data=diseaseMsgService.queryMsgListByUid(uid,page);
        List<Map<String, Object>> list = (List<Map<String, Object>>) data.getResult();
        list = list == null?new ArrayList():list;
        JsonListResponseEntity<Map<String, Object>> result = new JsonListResponseEntity<>();
        result.setCode(0);
        if(data.getHasNext()){
            result.setContent(list, true, null, String.valueOf(pageNo+1));
        }else{
            result.setContent(list, false, null, String.valueOf(pageNo));
        }
        return result;
    }

    /**
     * 根据类型查询列表数据
     *
     * @param input
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/type/list", method = RequestMethod.GET)
    public Object getTypeList(@RequestParam Map<String,Object> input){
        RequestDataReader reader = new RequestDataReader(input);
        String uid = reader.readString("uid",false);
        int pageNo = Integer.parseInt(reader.readDefaultString("flag","1"));
        int pageSize = Integer.parseInt(reader.readDefaultString("pageSize", "20"));
        String msgType = reader.readString("msgType",false);

        MsgTypeEnum.fromTypeCode(msgType);
        if(!msgType.equals(MsgTypeEnum.msgType6.getTypeCode())
                && !msgType.equals(MsgTypeEnum.msgType7.getTypeCode())
                && !msgType.equals(MsgTypeEnum.msgType8.getTypeCode())
                && !msgType.equals(MsgTypeEnum.msgType9.getTypeCode())){
            throw new EnumMatchException("消息类型["+msgType+"]不匹配.");
        }

        Page page=new Page(pageNo,pageSize);
        Page data=diseaseMsgService.queryMsgListByUidType(uid,page,msgType);
        List<Map<String, Object>> list = (List<Map<String, Object>>) data.getResult();
        list = list == null?new ArrayList():list;
        JsonListResponseEntity<Map<String, Object>> result = new JsonListResponseEntity<>();
        result.setCode(0);
        if(data.getHasNext()){
            result.setContent(list, true, null, String.valueOf(pageNo+1));
        }else{
            result.setContent(list, false, null, String.valueOf(pageNo));
        }
        return result;
    }
}
