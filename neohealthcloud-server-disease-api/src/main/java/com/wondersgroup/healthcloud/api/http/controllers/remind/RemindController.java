package com.wondersgroup.healthcloud.api.http.controllers.remind;

import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.jpa.entity.remind.Remind;
import com.wondersgroup.healthcloud.jpa.entity.remind.RemindItem;
import com.wondersgroup.healthcloud.jpa.entity.remind.RemindTime;
import com.wondersgroup.healthcloud.services.remind.RemindService;
import com.wondersgroup.healthcloud.services.remind.dto.RemindDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Admin on 2017/4/11.
 */
@RestController
@RequestMapping("/api/remind")
public class RemindController {

    @Autowired
    private RemindService remindService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public JsonListResponseEntity<RemindDTO> list(@RequestParam String userId,
                                                  @RequestParam(name = "flag", required = false, defaultValue = "0") int pageNo,
                                                  @RequestParam(name = "pageSize", required = false, defaultValue = "11") int pageSize) {
        JsonListResponseEntity<RemindDTO> result = new JsonListResponseEntity();
        List<RemindDTO> remindDTOs = remindService.list(userId, pageNo, pageSize);
        Boolean hasMore = false;
        if (remindDTOs != null && remindDTOs.size() > pageSize - 1) {
            hasMore = true;
        }
        result.setContent(remindDTOs, hasMore, null, pageNo + "");
        return result;
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public JsonResponseEntity<RemindDTO> detail(@RequestParam String id) {
        JsonResponseEntity<RemindDTO> result = new JsonResponseEntity();
        RemindDTO remindDTO = remindService.detail(id);
        if (remindDTO != null) {
            result.setData(remindDTO);
        } else {
            result.setMsg("未查询到相关记录");
        }
        return result;
    }

    @RequestMapping(value = "/saveAndUpdate", method = RequestMethod.POST)
    public JsonListResponseEntity saveAndUpdate(@RequestBody String remindJson) {
        JsonKeyReader remindReader = new JsonKeyReader(remindJson);
        String id = remindReader.readString("id", true);
        String userId = remindReader.readString("userId", false);
        String type = remindReader.readString("type", false);
        String remark = remindReader.readString("remark", false);
        RemindItem[] remindItems = remindReader.readObject("remindItems", true, RemindItem[].class);
        RemindTime[] remindTimes = remindReader.readObject("remindTimes", true, RemindTime[].class);
        RemindItem[] delRemindItems = remindReader.readObject("delRemindItems", true, RemindItem[].class);
        RemindTime[] delRemindTimes = remindReader.readObject("delRemindTimes", true, RemindTime[].class);
        Remind remind = new Remind(id, userId, type, remark);

        remindService.saveAndUpdate(remind, remindItems, remindTimes, delRemindItems, delRemindTimes);

        JsonListResponseEntity result = new JsonListResponseEntity();
        return result;
    }

    @RequestMapping(value = "/enableOrDisableRemind", method = RequestMethod.POST)
    public JsonResponseEntity enableAndDisableRemind(@RequestBody String remind) {
        JsonKeyReader reader = new JsonKeyReader(remind);
        String remindId = reader.readString("id", false);

        JsonResponseEntity result = new JsonResponseEntity();
        int rtnInt = remindService.enableOrDisableRemind(remindId);
        if (rtnInt == 0) {
            result.setMsg("修改提醒状态成功");
        } else {
            result.setCode(500);
            result.setMsg("修改提醒状态失败");
        }
        return result;
    }

    @RequestMapping(value = "/deleteRemind", method = RequestMethod.POST)
    public JsonResponseEntity deleteRemind(@RequestBody String remind) {
        JsonKeyReader reader = new JsonKeyReader(remind);
        String remindId = reader.readString("id", false);

        JsonResponseEntity result = new JsonResponseEntity();
        int rtnInt = remindService.deleteRemind(remindId);
        if (rtnInt == 0) {
            result.setMsg("删除提醒成功");
        } else {
            result.setCode(500);
            result.setMsg("删除提醒失败");
        }
        return result;
    }
}
