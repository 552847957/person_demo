package com.wondersgroup.healthcloud.api.http.controllers.user;

import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.helper.push.area.PushAreaService;
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
public class PushController {

    @Autowired
    private PushAreaService pushAreaService;

    @PostMapping(path = "/api/user/push/alias", produces = "application/json")
    @VersionRange
    public String pushAlias(@RequestHeader("main-area") String mainArea,
                            @RequestBody String request) {
        JsonKeyReader reader = new JsonKeyReader(request);
        String uid = reader.readString("uid", false);
        String cid = reader.readString("cid", false);

        pushAreaService.bindInfoAfterSignin(uid, cid, mainArea);

        String pushResponseTemplate = "{\"code\":0,\"data\":{\"alias\":\"%s\"}}";
        return String.format(pushResponseTemplate, uid);
    }

    @DeleteMapping(path = "/api/user/push", produces = "application/json")
    @VersionRange
    public String unbindPush(@RequestHeader("main-area") String mainArea,
                             @RequestParam String uid) {
        pushAreaService.unbindInfoAfterSignout(uid);
        return "{\"code\":0}";
    }
}
