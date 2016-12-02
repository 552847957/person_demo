package com.wondersgroup.healthcloud.api.http.controllers.bbs;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.api.utils.RequestDataReader;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.utils.DateUtils;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Fans;
import com.wondersgroup.healthcloud.services.bbs.*;
import com.wondersgroup.healthcloud.utils.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 粉丝关注接口
 * Created by jialing.yao on 2016-8-15.
 */
@RestController
@RequestMapping("/api/bbs/fans")
public class FansController {

    private static final Logger logger = LoggerFactory.getLogger("FansController");

    @Autowired
    FansService fansService;

    /**
     * 粉丝列表查询
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Object getFansList(@RequestParam Map<String, Object> input) {
        RequestDataReader reader = new RequestDataReader(input);
        String topUid = reader.readString("topUid", false);
        String clickUid = reader.readString("clickUid", true);
        int pageNo = Integer.parseInt(reader.readDefaultString("flag", "1"));
        int pageSize = Integer.parseInt(reader.readDefaultString("pageSize", "10"));

        Page page = new Page(pageNo, pageSize);
        List<Map<String, Object>> list = Lists.newArrayList();
        Page data = null;
        //查顶层粉丝列表
        if (StringUtils.isNotBlank(topUid) && StringUtils.isBlank(clickUid)) {
            data = fansService.queryFansListByTopUid(topUid, page);
            list = (List<Map<String, Object>>) data.getResult();
        }
        //查点击某个粉丝的粉丝列表
        if (StringUtils.isNotBlank(topUid) && StringUtils.isNotBlank(clickUid)) {
            data = fansService.queryFansListByClickUid(topUid, clickUid, page);
            list = (List<Map<String, Object>>) data.getResult();
        }
        JsonListResponseEntity<Map<String, Object>> result = new JsonListResponseEntity<>();
        result.setCode(0);
        if (data.getHasNext()) {
            result.setContent(list, true, null, String.valueOf(pageNo + 1));
        } else {
            result.setContent(list, false, null, String.valueOf(pageNo));
        }
        return result;
    }

    /**
     * created by limenghua at 2016.09.14
     *
     * @param input
     * @return
     */
    /**
     * 关注列表
     */
    @RequestMapping(value = "/getAttentList", method = RequestMethod.GET)
    public Object getAttentList(@RequestParam Map<String, Object> input) {
        JsonListResponseEntity<Map<String, Object>> result = new JsonListResponseEntity<>();
        try {
            RequestDataReader reader = new RequestDataReader(input);
            // 登录用户的id
            String topUid = reader.readString("topUid", false);
            // 点击查看用户的id
            String clickUid = reader.readString("clickUid", true);
            int pageNo = Integer.parseInt(reader.readDefaultString("flag", "1"));
            int pageSize = Integer.parseInt(reader.readDefaultString("pageSize", "10"));

            // 分页查询条件
            Page page = new Page(pageNo, pageSize);
            List<Map<String, Object>> list = Lists.newArrayList();
            Page data = null;

            // 查看登录用户的关注列表
            if (StringUtils.isNotBlank(topUid) && StringUtils.isBlank(clickUid)) {
                data = fansService.queryAttentListByTopUid(topUid, page);
                list = (List<Map<String, Object>>) data.getResult();
            }
            // 查询点击用户的关注列表
            if (StringUtils.isNotBlank(topUid) && StringUtils.isNotBlank(clickUid)) {
                data = fansService.queryAttentListByClickUid(topUid, clickUid, page);
                list = (List<Map<String, Object>>) data.getResult();
            }

            if (data.getHasNext()) {
                result.setContent(list, true, null, String.valueOf(pageNo + 1));
            } else {
                result.setContent(list, false, null, String.valueOf(pageNo));
            }
        } catch (NumberFormatException e) {
            String errorMsg = "查询我的关注列表出错";
            logger.error(errorMsg, e);
            result.setCode(1001);
            result.setMsg(errorMsg);
        }
        return result;
    }

    /**
     * 关注某人
     */
    @RequestMapping(value = "/attent", method = RequestMethod.POST)
    public JsonResponseEntity attentSomeone(@RequestBody String request) {
        JsonResponseEntity jsonResponseEntity = new JsonResponseEntity();
        JsonKeyReader reader = new JsonKeyReader(request);
        String uId = reader.readString("uId", false);
        String otherId = reader.readString("otherId", false);

        try {
            // 注意：关注别人，fans_uid 为自己
            Fans exist = fansService.queryByUIdAndFansUid(otherId, uId);
            if (exist != null) {// 已经关注过
                if ("0".equals(exist.getDelFlag())) {// 没有删除
                    jsonResponseEntity.setMsg("您已关注了该用户");
                    return jsonResponseEntity;
                }// end if
                if ("1".equals(exist.getDelFlag())) {
                    exist.setDelFlag("0");
                    exist.setUpdateTime(DateUtils.sdf.format(new Date()));
                    fansService.saveFans(exist);// 更新del_flag状态
                    jsonResponseEntity.setMsg("关注成功");
                    Map<String, Object> info = new HashMap<>();
                    info.put("attentStatus", fansService.ifAttentEachOther(otherId, uId) ? 1 : 0);
                    jsonResponseEntity.setData(info);
                    logger.info(String.format("[%s]重新关注[%s]成功", uId, otherId));
                    return jsonResponseEntity;
                }// end if
            } else {
                Fans fans = initFans(uId, otherId);
                fansService.saveFans(fans);
                jsonResponseEntity.setMsg("关注成功");
                Map<String, Object> info = new HashMap<>();
                info.put("attentStatus", fansService.ifAttentEachOther(otherId, uId) ? 1 : 0);
                jsonResponseEntity.setData(info);
                logger.info(String.format("[%s]关注[%s]成功", uId, otherId));
                return jsonResponseEntity;
            }// end else
        } catch (Exception e) {
            String errorMsg = "关注用户出错";
            logger.error(errorMsg, e);
            jsonResponseEntity.setCode(1001);
            jsonResponseEntity.setMsg(errorMsg);
        }
        return jsonResponseEntity;
    }

    private Fans initFans(String uId, String otherId) {
        Fans fans = new Fans();
        fans.setUId(otherId);
        fans.setFansUid(uId);
        fans.setCreateTime(DateUtils.sdf.format(new Date()));
        fans.setUpdateTime(DateUtils.sdf.format(new Date()));
        return fans;
    }

    /**
     * 取消关注
     *
     * @param uId
     * @param otherId
     * @return
     */
    @RequestMapping(value = "/attent", method = RequestMethod.DELETE)
    public JsonResponseEntity attentSomeone(@RequestParam String uId, @RequestParam String otherId) {
        JsonResponseEntity jsonResponseEntity = new JsonResponseEntity();
        try {
            // 注意，查询关注，fansUid为自己
            Fans exist = fansService.queryByUIdAndFansUidAndDelFlag(otherId, uId, "0");
            if (exist != null) {
                exist.setDelFlag("1");
                exist.setUpdateTime(DateUtils.sdf.format(new Date()));
                fansService.saveFans(exist);
                jsonResponseEntity.setMsg("取消关注成功");
                logger.info(String.format("[%s]取消关注[%s]", uId, otherId));
                return jsonResponseEntity;
            } else {
                jsonResponseEntity.setMsg("您未关注该用户");
                return jsonResponseEntity;
            }
        } catch (Exception e) {
            String errorMsg = "取消关注出错";
            logger.error(errorMsg, e);
            jsonResponseEntity.setCode(1001);
            jsonResponseEntity.setMsg(errorMsg);
        }
        return jsonResponseEntity;
    }
}
