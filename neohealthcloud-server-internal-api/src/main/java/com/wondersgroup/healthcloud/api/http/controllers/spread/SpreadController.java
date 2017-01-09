package com.wondersgroup.healthcloud.api.http.controllers.spread;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.exceptions.CommonException;
import com.wondersgroup.healthcloud.jpa.entity.spread.Evangelist;
import com.wondersgroup.healthcloud.jpa.repository.spread.EvangelistRepository;
import com.wondersgroup.healthcloud.services.localspread.LocalSpreadService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by nick on 2016/12/23.
 */
@RestController
@RequestMapping("/api/evangelist")
public class SpreadController {

    @Autowired
    private EvangelistRepository evangelistRepository;

    @Autowired
    private LocalSpreadService localSpreadService;

    @RequestMapping(value = "/validate", method = RequestMethod.GET)
    public JsonResponseEntity validate(@RequestParam String staff_id) {
        JsonResponseEntity responseEntity = new JsonResponseEntity();
        if (evangelistRepository.findByStaffId(staff_id) != null) {
            throw new CommonException(1000, "该工号信息已存在");
        }
        responseEntity.setMsg("该工号可用");
        return responseEntity;
    }

    @PostMapping(path = "/new")
    public JsonResponseEntity saveEvangelist(@RequestBody Evangelist evangelist) {
        JsonResponseEntity responseEntity = new JsonResponseEntity();
        if (evangelist == null
                || StringUtils.isEmpty(evangelist.getName())
                || StringUtils.isEmpty(evangelist.getStaffId())) {
            throw new CommonException(1000, "信息缺失，请完善数据后提交！");
        }
        if (evangelistRepository.findByStaffId(evangelist.getStaffId()) != null) {
            throw new CommonException(1000, "该工号信息已存在");
        }
        localSpreadService.saveAndUpdate(evangelist);
        responseEntity.setMsg("数据保存成功！");
        return responseEntity;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public JsonResponseEntity list(@RequestParam(name = "name", required = false) String name,
                                   @RequestParam(name = "staff_id", required = false) String staffId,
                                   @RequestParam(name = "spread_code", required = false) String spreadCode) {
        JsonResponseEntity result = new JsonResponseEntity();
        Evangelist evangelist = new Evangelist();
        if (StringUtils.isNotEmpty(name)) {
            evangelist.setName(name);
        }
        if (StringUtils.isNotEmpty(staffId)) {
            evangelist.setStaffId(staffId);
        }
        if (StringUtils.isNotEmpty(spreadCode)) {
            evangelist.setSpreadCode(spreadCode);
        }
        List<Evangelist> rtnList = localSpreadService.list(evangelist);
        if (rtnList != null && rtnList.size() > 0) {
            result.setData(rtnList);
        } else {
            result.setMsg("未查询到相关数据！");
        }
        return result;
    }
}
