package com.wondersgroup.healthcloud.api.http.controllers.sign;

import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.services.sign.SignDTO;
import com.wondersgroup.healthcloud.services.sign.SignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * Created by ZZX on 2017/6/9.
 */
@RestController
@RequestMapping("/api/doctor/sign")
public class SignController {

    private static final Logger logger = LoggerFactory.getLogger(SignController.class);

    @Autowired
    private SignService signService;

    /**
     * 签约居民列表
     * @param name
     * @param pageNo
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/userLists", method = RequestMethod.GET)
    @VersionRange
    public JsonListResponseEntity userLists(@RequestParam(required = false) String name,
                                            @RequestParam(required = false, defaultValue = "0", name = "flag") int pageNo,
                                            @RequestParam(required = false, defaultValue = "20") int pageSize) {
        JsonListResponseEntity responseEntity = new JsonListResponseEntity();
        pageSize += 1;
        List<SignDTO> list = signService.userLists(name, pageNo, pageSize);
        boolean hasMore = false;
        if (list != null && list.size() > pageSize - 1) {
            hasMore = true;
            pageNo += 1;
            list = list.subList(0, pageSize -1);
        }

        responseEntity.setContent(list, hasMore, null, Integer.toString(pageNo));
        return responseEntity;
    }
}
