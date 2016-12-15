package com.wondersgroup.healthcloud.api.http.controllers.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wondersgroup.healthcloud.api.http.dto.imagetext.MonitorImageTextDTO;
import com.wondersgroup.healthcloud.exceptions.Exceptions;
import com.wondersgroup.healthcloud.jpa.entity.imagetext.ImageText;
import com.wondersgroup.healthcloud.jpa.entity.user.monitor.UserMonitor;
import com.wondersgroup.healthcloud.services.imagetext.ImageTextService;
import com.wondersgroup.healthcloud.services.user.UserMonitorService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by zhaozhenxing on 2016/12/13.
 */

@RestController
@RequestMapping("/api/userMonitor")
public class UserMonitorController {
    private static final Logger log = Logger.getLogger(UserMonitorController.class);

    @Autowired
    private UserMonitorService userMonitorService;

    @Autowired
    private ImageTextService imageTextService;

    @GetMapping(value = "/findMonitors")
    public JsonResponseEntity<List<MonitorImageTextDTO>> findImageTextByAdcode(@RequestHeader(defaultValue = "3101") String mainArea,
                                                                               @RequestHeader(defaultValue = "1") String source,
                                                                               @RequestParam(defaultValue = "12") Integer adcode,
                                                                               @RequestParam String uid) {
        JsonResponseEntity<List<MonitorImageTextDTO>> result = new JsonResponseEntity<>();

        Map<String, Object> param = new HashMap<>();

        param.put("mainArea", mainArea);
        param.put("source", source);
        param.put("adcode", adcode);

        List<MonitorImageTextDTO> rtnList = null;
        try {
            List<ImageText> imageTextList = imageTextService.findImageTextByAdcode(1, 999, param);
            if (imageTextList != null && imageTextList.size() > 0) {
                UserMonitor userMonitor = userMonitorService.detail(uid);
                String monitorId = null;
                if (userMonitor != null && StringUtils.isNotEmpty(userMonitor.getMonitorId())) {
                    monitorId = userMonitor.getMonitorId();
                }
                rtnList = new ArrayList<>();
                for (ImageText imageText : imageTextList) {
                    MonitorImageTextDTO mitDTO = new MonitorImageTextDTO(imageText);
                    if (StringUtils.isNotEmpty(monitorId) && monitorId.equals(imageText.getId())) {
                        mitDTO.setChecked(true);
                    }
                    rtnList.add(mitDTO);
                }
            }
        } catch (Exception ex) {
            result.setCode(1000);
            result.setMsg("获取数据失败");
            log.error(Exceptions.getErrorMessageWithNestedException(ex));
        }
        result.setData(rtnList == null ? new ArrayList<MonitorImageTextDTO>() : rtnList);
        return result;
    }

    @GetMapping(value = "/findUserMonitor")
    public JsonResponseEntity<MonitorImageTextDTO> findUserMonitor(@RequestHeader(defaultValue = "3101") String mainArea,
                                                                               @RequestHeader(defaultValue = "1") String source,
                                                                               @RequestParam(defaultValue = "12") Integer adcode,
                                                                               @RequestParam String uid) {
        JsonResponseEntity<MonitorImageTextDTO> result = new JsonResponseEntity<>();
        if (StringUtils.isEmpty(uid)) {
            result.setCode(1000);
            result.setMsg("信息缺失，请完善后提交！");
            return result;
        }
        UserMonitor userMonitor = userMonitorService.detail(uid);
        if (userMonitor != null && StringUtils.isNotEmpty(userMonitor.getMonitorId())) {
            ImageText imageText = imageTextService.findImageTextById(userMonitor.getMonitorId());
            if (imageText != null) {
                MonitorImageTextDTO mitDTO = new MonitorImageTextDTO(imageText);
                result.setData(mitDTO);
                return result;
            }
        }

        result.setCode(1000);
        result.setMsg("未查询到相关记录");
        return result;
    }

    @RequestMapping(value = "/saveAndUpdate", method = RequestMethod.POST)
    public JsonResponseEntity saveAndUpdate(@RequestBody UserMonitor userMonitor) {
        JsonResponseEntity result = new JsonResponseEntity();
        if (userMonitor == null || StringUtils.isEmpty(userMonitor.getUid()) || StringUtils.isEmpty(userMonitor.getMonitorId())) {
            result.setCode(1000);
            result.setMsg("信息缺失，请完善后提交！");
            return result;
        }
        UserMonitor rtnUserMonitor = userMonitorService.saveAndUpdate(userMonitor);
        if(rtnUserMonitor != null) {
            result.setMsg("数据保存成功?");
        } else {
            result.setCode(1000);
            result.setMsg("数据保存失败?");
        }
        return result;
    }

}