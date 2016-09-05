package com.wondersgroup.healthcloud.api.http.controllers.doctor;

import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.helper.push.area.PushAreaBindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * ░░░░░▄█▌▀▄▓▓▄▄▄▄▀▀▀▄▓▓▓▓▓▌█
 * ░░░▄█▀▀▄▓█▓▓▓▓▓▓▓▓▓▓▓▓▀░▓▌█
 * ░░█▀▄▓▓▓███▓▓▓███▓▓▓▄░░▄▓▐█▌
 * ░█▌▓▓▓▀▀▓▓▓▓███▓▓▓▓▓▓▓▄▀▓▓▐█
 * ▐█▐██▐░▄▓▓▓▓▓▀▄░▀▓▓▓▓▓▓▓▓▓▌█▌
 * █▌███▓▓▓▓▓▓▓▓▐░░▄▓▓███▓▓▓▄▀▐█
 * █▐█▓▀░░▀▓▓▓▓▓▓▓▓▓██████▓▓▓▓▐█
 * ▌▓▄▌▀░▀░▐▀█▄▓▓██████████▓▓▓▌█▌
 * ▌▓▓▓▄▄▀▀▓▓▓▀▓▓▓▓▓▓▓▓█▓█▓█▓▓▌█▌
 * █▐▓▓▓▓▓▓▄▄▄▓▓▓▓▓▓█▓█▓█▓█▓▓▓▐█
 * <p>
 * Created by zhangzhixiu on 8/8/16.
 */
@RestController
@RequestMapping
public class PushController {

    @Autowired
    private PushAreaBindService pushAreaBindService;

    @PostMapping(path = "/api/utils/push/alias", produces = "application/json")
    @VersionRange
    public String pushAlias(@RequestHeader("main-area") String mainArea,
                            @RequestBody String request) {
        JsonKeyReader reader = new JsonKeyReader(request);
        String uid = reader.readString("uid", false);
        String cid = reader.readString("cid", false);

        String pushResponseTemplate = "{\"code\":0,\"data\":{\"alias\":\"%s\"}}";
        return String.format(pushResponseTemplate, uid);
    }

    @RequestMapping(path = "/api/utils/push", method = RequestMethod.DELETE, produces = "application/json")
    @WithoutToken
    @VersionRange
    public String unbindPush(@RequestHeader("main-area") String mainArea,
                             @RequestParam String uid) {
        return "{\"code\":0}";
    }
}
