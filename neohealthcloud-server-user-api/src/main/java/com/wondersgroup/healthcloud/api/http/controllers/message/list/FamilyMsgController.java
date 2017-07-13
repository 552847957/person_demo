package com.wondersgroup.healthcloud.api.http.controllers.message.list;

import com.wondersgroup.healthcloud.api.utils.CommonUtils;
import com.wondersgroup.healthcloud.api.utils.RequestDataReader;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.services.user.message.MsgService;
import com.wondersgroup.healthcloud.utils.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 消息列表-家庭消息接口
 * Created by jialing.yao on 2016-12-12.
 */
@RestController
@RequestMapping("/api/family/message")
public class FamilyMsgController {
    @Autowired
    private MsgService familyMsgService;

    /**
     * 家庭消息列表查询
     */
    @VersionRange
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Object getList(@RequestParam Map<String,Object> input,
                          @RequestHeader("version") String version){
        RequestDataReader reader = new RequestDataReader(input);
        String uid=reader.readString("uid",false);
        int pageNo=Integer.parseInt(reader.readDefaultString("flag","1"));
        int pageSize=Integer.parseInt(reader.readDefaultString("pageSize", "10"));

        Page page=new Page(pageNo,pageSize);
//        Boolean isSetRead = true;
//        if(CommonUtils.compareVersion("4.3",version)){//兼容老版本 新版本是点击一下红点消失 老版本是拉取一个列表红点消失
//            isSetRead = false;
//        }
        Page data=familyMsgService.queryMsgListByUid(uid,page);
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
