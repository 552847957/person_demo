package com.wondersgroup.healthcloud.api.http.controllers.remind;

import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.jpa.entity.medicine.CommonlyUsedMedicine;
import com.wondersgroup.healthcloud.jpa.entity.remind.Remind;
import com.wondersgroup.healthcloud.jpa.entity.remind.RemindItem;
import com.wondersgroup.healthcloud.jpa.entity.remind.RemindTime;
import com.wondersgroup.healthcloud.jpa.repository.remind.CommonlyUsedMedicineRepository;
import com.wondersgroup.healthcloud.services.remind.CommonlyUsedMedicineService;
import com.wondersgroup.healthcloud.services.remind.RemindService;
import com.wondersgroup.healthcloud.services.remind.dto.RemindDTO;
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

    @Autowired
    private CommonlyUsedMedicineService commonlyUsedMedicineService;

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

        JsonListResponseEntity result = new JsonListResponseEntity();
        int rtnInt = remindService.saveAndUpdate(remind, remindItems, remindTimes, delRemindItems, delRemindTimes);
        if (rtnInt == 0) {
            result.setMsg("保存用药提醒成功");
        } else {
            result.setCode(500);
            result.setMsg("保存用药提醒失败");
        }
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

    @RequestMapping(value = "/listCUMs", method = RequestMethod.GET)
    public JsonListResponseEntity<CommonlyUsedMedicine> listCUMs(@RequestParam String userId, @RequestParam String type) {
        JsonListResponseEntity<CommonlyUsedMedicine> result = new JsonListResponseEntity<>();
        List<CommonlyUsedMedicine> commonlyUsedMedicines =commonlyUsedMedicineService.listTop5(userId, type);
        result.setContent(commonlyUsedMedicines);
        return result;
    }

    @RequestMapping(value = "/deleteCUM", method = RequestMethod.POST)
    public JsonResponseEntity deleteCUM(@RequestBody String cumJson) {
        JsonKeyReader reader = new JsonKeyReader(cumJson);
        String id = reader.readString("id", false);

        JsonResponseEntity result = new JsonResponseEntity();
        int rtnInt = commonlyUsedMedicineService.delete(id);
        if (rtnInt == 0) {
            result.setMsg("删除常用药品成功");
        } else {
            result.setCode(500);
            result.setMsg("删除常用药品失败");
        }
        return result;
    }

}
