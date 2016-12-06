package com.wondersgroup.healthcloud.api.http.controllers.bbs;

import com.wondersgroup.healthcloud.api.utils.RequestDataReader;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.services.bbs.*;
import com.wondersgroup.healthcloud.utils.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 消息中心(动态消息列表、系统通知列表)
 * Created by jialing.yao on 2016-8-17.
 */
@RestController
@RequestMapping("/api/bbs")
public class MsgController {
    @Autowired
    MsgService dynamicMsgService;
    @Autowired
    MsgService sysMsgService;

    /**
     * 动态消息列表查询
     */
    @VersionRange
    @RequestMapping(value = "/msg/dynamic/list", method = RequestMethod.GET)
    public Object getDynamicList(@RequestParam Map<String,Object> input){
        RequestDataReader reader = new RequestDataReader(input);
        String uid=reader.readString("uid",false);
        int pageNo=Integer.parseInt(reader.readDefaultString("flag","1"));
        int pageSize=Integer.parseInt(reader.readDefaultString("pageSize", "10"));

        Page page=new Page(pageNo,pageSize);
        Page data=dynamicMsgService.queryMsgListByUid(uid,page);
        List<Map<String, Object>> list = (List<Map<String, Object>>) data.getResult();
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
     * 系统消息列表查询
     */
    @VersionRange
    @RequestMapping(value = "/msg/sysnotice/list", method = RequestMethod.GET)
    public Object getSysList(@RequestParam Map<String,Object> input){
        RequestDataReader reader = new RequestDataReader(input);
        String uid=reader.readString("uid",false);
        int pageNo=Integer.parseInt(reader.readDefaultString("flag","1"));
        int pageSize=Integer.parseInt(reader.readDefaultString("pageSize", "10"));

        Page page=new Page(pageNo,pageSize);
        Page data=sysMsgService.queryMsgListByUid(uid,page);
        List<Map<String, Object>> list = (List<Map<String, Object>>) data.getResult();
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
