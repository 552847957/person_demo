package com.wondersgroup.healthcloud.api.http.controllers.medicine;

import java.util.List;

import com.wondersgroup.healthcloud.jpa.entity.medicine.Medicine;
import com.wondersgroup.healthcloud.services.medicine.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by zhaozhenxing on 2017/04/15.
 */

@RestController
@RequestMapping("/api/medicine")
public class MedicineController {
    @Autowired
    private MedicineService medicineService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public JsonResponseEntity list(String type) {
        JsonResponseEntity result = new JsonResponseEntity();
        List<Medicine> rtnList = medicineService.list(type);
        if(rtnList != null && rtnList.size() > 0) {
            result.setData(rtnList);
        } else {
            result.setMsg("未查询到相关数据！");
        }
        return result;
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public JsonResponseEntity detail(@RequestParam(name = "id") String id) {
        JsonResponseEntity result = new JsonResponseEntity();
        Medicine medicine = medicineService.detail(id);
        if(medicine != null) {
            result.setData(medicine);
        } else {
            result.setMsg("未查询到相关数据！");
        }
        return result;
    }

    @RequestMapping(value = "/saveAndUpdate", method = RequestMethod.POST)
    public JsonResponseEntity saveAndUpdate(@RequestBody Medicine medicine) {
        JsonResponseEntity result = new JsonResponseEntity();
        Medicine rtnMedicine = medicineService.saveAndUpdate(medicine);
        if(rtnMedicine != null) {
            result.setMsg("数据保存成功！");
        } else {
            result.setCode(1000);
            result.setMsg("数据保存失败！");
        }
        return result;
    }

}