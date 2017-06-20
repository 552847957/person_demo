package com.wondersgroup.healthcloud.api.http.controllers.resident;

import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.diabetes.DoctorTubeSignUser;
import com.wondersgroup.healthcloud.services.disease.DoctorTubeSignUserService;
import com.wondersgroup.healthcloud.services.disease.dto.ResidentCondition;
import com.wondersgroup.healthcloud.services.disease.dto.ResidentInfoDto;
import org.apache.commons.lang3.StringUtils;
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
            @RequestParam(required = true) String doctorId,
            @RequestParam(required = false) Integer signed,
            @RequestParam(required = false) String peopleType,
            @RequestParam(required = false) String diseaseType,
            @RequestParam(required = false) String kw,
            @RequestParam(required = false, defaultValue = "1") Integer flag,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        JsonListResponseEntity response = new JsonListResponseEntity();
        Page<DoctorTubeSignUser> pageData = null;
        ResidentCondition residentCondition = new ResidentCondition(doctorId, flag, pageSize, signed, peopleType, diseaseType, kw);

        // 关键字搜索
        if (StringUtils.isNotBlank(kw)) {
            List<DoctorTubeSignUser> tubeList = doctorTubeSignUserService.kwSearchList(kw, flag, pageSize);
            int count = (int) doctorTubeSignUserService.kwSearchCount(kw);
            int pages = 0;
            if (count % pageSize == 0) {
                pages = count / pageSize;
            } else {
                pages = (count / pageSize) + 1;
            }
            boolean more = false;
            // 总页数>当前页码
            if (pages > flag) {
                more = true;
            }
            response.setContent(doctorTubeSignUserService.dbListToDtoList(tubeList), more, null, "" + flag);
            return response;
        } else {
            pageData = doctorTubeSignUserService.search(residentCondition);
        }

        List<ResidentInfoDto> listData = doctorTubeSignUserService.pageDataToDtoList(pageData);

        if (listData.size() > 0) {
            boolean more = false;
            // 总页数>当前页码
            if (pageData.getTotalPages() > flag) {
                more = true;
            }
            response.setContent(listData, more, null, "" + flag++);
        } else {
            response.setContent(listData, false, null, "" + flag++);
        }

        return response;
    }
}
