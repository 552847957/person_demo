package com.wondersgroup.healthcloud.api.http.controllers.disease;

import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.diabetes.DoctorTubeSignUser;
import com.wondersgroup.healthcloud.services.disease.DoctorTubeSignUserService;
import com.wondersgroup.healthcloud.services.disease.dto.ResidentCondition;
import com.wondersgroup.healthcloud.services.disease.dto.ResidentInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by limenghua on 2017/6/7.
 *
 * @author limenghua
 */
@RestController
@RequestMapping(value = "/api/resident")
public class ResidentController {

    @Autowired
    private DoctorTubeSignUserService doctorTubeSignUserService;

    @GetMapping("/list")
    public JsonListResponseEntity list(
            @RequestParam(required = true) String famId,
            @RequestParam(required = false) Integer signed,
            @RequestParam(required = false) String peopleType,
            @RequestParam(required = false) String diseaseType,
            @RequestParam(required = false) String kw,
            @RequestParam(required = false, defaultValue = "1") Integer flag,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        JsonListResponseEntity response = new JsonListResponseEntity();

        ResidentCondition residentCondition = new ResidentCondition(famId, flag, pageSize, signed, peopleType, diseaseType, kw);

        Page<DoctorTubeSignUser> pageData = doctorTubeSignUserService.search(residentCondition);

        List<ResidentInfoDto> listData = doctorTubeSignUserService.pageDataToDtoList(pageData);

        if (listData.size() > 0) {
            boolean more = false;
            // 总页数>当前页码
            if (pageData.getTotalPages() > flag) {
                more = true;
            }
            response.setContent(listData, more, null, "" + flag);
        } else {
            response.setContent(listData, false, null, "" + flag);
        }

        return response;
    }
}
