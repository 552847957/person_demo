package com.wondersgroup.healthcloud.api.http.controllers.logger;

import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 行为日志接口
 * Created by jialing.yao on 2016-12-20.
 */
@RestController
@RequestMapping(path = "/api/log")
public class ActionLogController {

    @PostMapping(path = "/action")
    @VersionRange
    @WithoutToken
    public JsonResponseEntity<Boolean> actionLog(@RequestBody String body) {
        //body 暂不做处理,以后可扩展为HTTP方式转发日志
        //TODO call logserer-httpmodel

        return new JsonResponseEntity<>(0, "上传成功", true);
    }
}
