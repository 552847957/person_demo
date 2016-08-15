package com.wondersgroup.healthcloud.api.http.controllers.help;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.wondersgroup.healthcloud.api.utils.PropertyFilterUtil;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.help.HelpCenter;
import com.wondersgroup.healthcloud.services.help.HelpCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shenbin on 16/8/12.
 */
@RestController
@RequestMapping("/api")
public class HelpCenterController {

    @Autowired
    private HelpCenterService helpCenterService;

    /**
     * 查询帮助中心
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/helpCenter", method = RequestMethod.GET)
    public String findHelpCenter() throws JsonProcessingException {
        List<HelpCenter> helpCenterList = helpCenterService.findByIsVisable("0");

        Map<Class, Object> filterMap = new HashMap<>();
        filterMap.put(HelpCenter.class, new String[]{"title", "content"});
        SimpleFilterProvider filterProvider = PropertyFilterUtil.filterOutAllExceptFilter(filterMap);

        if (helpCenterList != null && !helpCenterList.isEmpty()) {
            return PropertyFilterUtil
                    .getObjectMapper()
                    .setFilterProvider(filterProvider)
                    .writeValueAsString(new JsonResponseEntity(0, "查询成功", helpCenterList));
        } else {
            ObjectNode objectNode = JsonNodeFactory.instance.objectNode().put("info", 0);
            return new JsonResponseEntity(-1, "无数据", objectNode).toString();
        }
    }
}
