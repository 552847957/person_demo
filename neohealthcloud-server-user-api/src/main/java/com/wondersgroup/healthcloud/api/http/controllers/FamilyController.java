package com.wondersgroup.healthcloud.api.http.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wondersgroup.common.image.utils.ImagePath;
import com.wondersgroup.common.image.utils.ImageUploader;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.session.AccessToken;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.helper.family.FamilyMemberAccess;
import com.wondersgroup.healthcloud.jpa.entity.user.AnonymousAccount;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.entity.user.member.FamilyMember;
import com.wondersgroup.healthcloud.jpa.entity.user.member.FamilyMemberInvitation;
import com.wondersgroup.healthcloud.services.user.AnonymousAccountService;
import com.wondersgroup.healthcloud.services.user.FamilyService;
import com.wondersgroup.healthcloud.services.user.UserAccountService;
import com.wondersgroup.healthcloud.services.user.UserService;
import com.wondersgroup.healthcloud.services.user.dto.Session;
import com.wondersgroup.healthcloud.services.user.dto.member.FamilyMemberAPIEntity;
import com.wondersgroup.healthcloud.services.user.dto.member.FamilyMemberInvitationAPIEntity;

/**
 * 孫海迪
 * Created by sunhaidi on 2016.8.10
 */

@RestController
@RequestMapping("/api/family")
public class FamilyController {

    @Autowired
    private UserAccountService      accountService;

    @Autowired
    private UserService             userService;

    @Autowired
    private FamilyService           familyService;

    @Autowired
    private AnonymousAccountService anonymousAccountService;

    //    @Autowired
    //    private AnomalyIndexRepository anomalyIndexRepo;

    /**
     * 申请添加为亲情账户
     * @param request
     * @return JsonResponseEntity<String>
     */
    @RequestMapping(value = "/member", method = RequestMethod.POST)
    @VersionRange
    public JsonResponseEntity<String> inviteMember(@RequestBody String request) {
        JsonKeyReader reader = new JsonKeyReader(request);
        String id = reader.readString("uid", false);
        String mobile = reader.readString("mobile", true);
        String memberId = reader.readString("member_id", true);
        String relation = reader.readString("relation", false);
        String relationName = reader.readString("relation_name", true);
        String memo = StringUtils.defaultString(reader.readString("memo", true));
        Boolean recordReadable = reader.readDefaultBoolean("record_readable", false);

        JsonResponseEntity<String> body = new JsonResponseEntity<>();

        if (mobile == null && memberId == null) {
            body.setCode(1000);
            body.setMsg("[mobile]与[member_id]字段不能都为空");
            return body;
        }

        if (mobile != null && mobile.length() != 11) {
            body.setCode(1000);
            body.setMsg("请输入11位的手机号");
            return body;
        }

        if (memberId == null) {
            JsonResponseEntity<Map<String, String>> info = tinyInfo(mobile);
            if (info.getCode() != 0) {
                body.setCode(1000);
                body.setMsg("无相关账户");
                return body;
            }
        }
        familyService.inviteMember(id, memberId, mobile, memo, relation, relationName, recordReadable);
        body.setMsg("申请提交成功");
        return body;
    }

    /**
     * 账户简讯查询
     * @param mobile
     * @return JsonResponseEntity<Map<String,String>>
     */
    @RequestMapping(value = "/member/info", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<Map<String, String>> tinyInfo(@RequestParam String mobile) {
        JsonResponseEntity<Map<String, String>> body = new JsonResponseEntity<>();
        Map<String, String> data = Maps.newHashMap();
        try {
            RegisterInfo register = userService.findRegisterInfoByMobile(mobile);
            if (register.getHeadphoto() != null) {
                data.put("avatar", register.getHeadphoto() + ImagePath.avatarPostfix());
            }
            data.put("nickname", register.getNickname());
        } catch (Exception e) {
            //                if (accountService.checkAccount(mobile)) {
            //                    RegisterInfo register = accountService.fetchInfo(mobile);
            //                    data.put("avatar", register.getHeadphoto() + ImagePath.avatarPostfix());
            //                    data.put("nickname", register.getNickname());
            //                } else {
            throw e;
            //                }
        }
        body.setData(data);
        return body;
    }

    /**
     * 帮助对方注册
     * @param uid
     * @param mobile
     * @param verifyCode
     * @param password
     * @param relation
     * @param relationName
     * @param recordReadable
     * @param avatar
     * @return JsonResponseEntity<String>
     */
    @RequestMapping(value = "/member/registration", method = RequestMethod.POST)
    @VersionRange
    public JsonResponseEntity<String> helpRegistration(
            @RequestParam String uid, 
            @RequestParam String mobile,
            @RequestParam("verify_code") String verifyCode,
            @RequestParam String password,
            @RequestParam String relation,
            @RequestParam(value = "relation_name", required = false) String relationName,
            @RequestParam(value = "record_readable", defaultValue = "false") Boolean recordReadable,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
        String path = null;
        if (avatar != null) {
            try {
                path = ImageUploader.upload("app", IdGen.uuid(), avatar.getBytes());
            } catch (IOException ex) {
                //ignore
            }
        }
        familyService.helpRegistration(uid, mobile, password, verifyCode, path, null, relation, relationName,
                recordReadable);
        JsonResponseEntity<String> body = new JsonResponseEntity<>();
        body.setMsg("注册成功, 并已添加到亲情账户");
        return body;
    }

    /**
     * 发送注册短信验证码
     * @param uid
     * @param mobile
     * @param relation
     * @param relationName
     * @return JsonResponseEntity<String>
     */
    @RequestMapping(value = "/member/registration/code", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<String> registrationCode(@RequestParam String uid, @RequestParam String mobile,
            @RequestParam String relation, @RequestParam("relation_name") String relationName) {
        familyService.sendRegistrationCode(uid, relation, relationName, mobile);
        JsonResponseEntity<String> body = new JsonResponseEntity<>();
        body.setMsg("发送成功");
        return body;
    }

    /**
     * 申请列表
     * @param uid
     * @return JsonListResponseEntity<FamilyMemberInvitationAPIEntity>
     */
    @RequestMapping(value = "/member/todo", method = RequestMethod.GET)
    @VersionRange
    public JsonListResponseEntity<FamilyMemberInvitationAPIEntity> invitationList(@RequestParam String uid) {
        List<FamilyMemberInvitation> invitations = familyService.getInvitationList(uid);
        List<FamilyMemberInvitationAPIEntity> data = Lists.newLinkedList();
        for (FamilyMemberInvitation invitation : invitations) {
            RegisterInfo register = uid.equals(invitation.getUid()) ? userService.getOneNotNull(invitation
                    .getMemberId()) : userService.getOneNotNull(invitation.getUid());
            FamilyMember member = familyService.getFamilyMemberWithOrder(uid, invitation.getMemberId());
            int isAnonymous = null == member ? 0 : member.getIsAnonymous();
            if (uid.equals(invitation.getUid()) && null != member && null != member.getMemo()) {
                invitation.setRelationName(member.getMemo());
            }
            FamilyMemberInvitationAPIEntity entity = new FamilyMemberInvitationAPIEntity(invitation, uid, register,
                    isAnonymous);
            data.add(entity);
        }
        JsonListResponseEntity<FamilyMemberInvitationAPIEntity> body = new JsonListResponseEntity<>();
        body.setContent(data, false, null, null);
        return body;
    }

    /**
     * 同意/拒绝
     * @param request
     * @param token
     * @return JsonResponseEntity<String>
     */
    @RequestMapping(value = "/member/invitation", method = RequestMethod.POST)
    @VersionRange
    public JsonResponseEntity<String> agreeInvitation(@RequestBody String request
            , @AccessToken Session session
            ) {
        String uid = session.getUserId();
        JsonKeyReader reader = new JsonKeyReader(request);
//        String uid = reader.readString("uid", false);
        String id = reader.readString("invitation_id", false);
        Boolean agree = reader.readBoolean("agree", false);
        String relationName = reader.readString("relation_name", true);
        Boolean recordReadable = reader.readDefaultBoolean("record_readable", true);

        familyService.dealWithMemberInvitation(id, uid, agree, relationName, recordReadable);
        JsonResponseEntity<String> body = new JsonResponseEntity<>();
        body.setMsg(agree ? "添加成功" : "拒绝成功");
        return body;
    }

    /**
     * 成员列表
     * @param uid
     * @param memberId
     * @return JsonListResponseEntity<FamilyMemberAPIEntity>
     */
    @RequestMapping(value = "/member", method = RequestMethod.GET)
    @VersionRange
    public JsonListResponseEntity<FamilyMemberAPIEntity> memberList(@RequestParam String uid,
            @RequestParam(value = "member_id", required = false) String memberId) {

        List<FamilyMember> familyMembers;
        if (memberId == null) {
            familyMembers = familyService.getFamilyMembers(uid);
        } else {
            FamilyMember familyMember = familyService.getFamilyMemberWithOrder(uid, memberId);
            if (familyMember == null) {
                familyMembers = new ArrayList<>(0);
            } else {
                familyMembers = Lists.newArrayList(familyMember);
            }
        }
        List<FamilyMemberAPIEntity> data = Lists.newLinkedList();
        for (FamilyMember familyMember : familyMembers) {
            FamilyMemberAPIEntity entity;
            if (familyMember.getIsAnonymous() == 0) {
                RegisterInfo register = userService.getOneNotNull(familyMember.getMemberId());
                entity = new FamilyMemberAPIEntity(familyMember, register);
                FamilyMember familyMemb = familyService.getFamilyMemberWithOrder(familyMember.getMemberId(),
                        familyMember.getUid());
                entity.setRecordReadable(FamilyMemberAccess.recordReadable(familyMemb.getAccess()));
                entity.setRedirectFlag(0);
                entity.setHealthWarning(false);
            } else {
                AnonymousAccount anonymousAccount = anonymousAccountService.getAnonymousAccount(
                        familyMember.getMemberId(), false);
                entity = new FamilyMemberAPIEntity(familyMember, anonymousAccount);
                entity.setRecordReadable(true);
                if (anonymousAccount.getIdcard() == null) {
                    //                    JsonNode submitInfo = accountService.verficationSubmitInfo(anonymousAccount.getId(), true);
                    //                    Integer status = submitInfo.get("status").asInt();
                    //                    entity.setRedirectFlag(status - 1);
                    //                    entity.setHealthWarning(false);
                } else {
                    entity.setRedirectFlag(0);
                    entity.setHealthWarning(false);
                }
            }
            switch (entity.getRedirectFlag()) {
            case 0:
                //                if (0 != anomalyIndexRepo.findByUidAndDay(entity.getUid(), DateFormatter.dateFormat(new Date())).size()) {
                //                    entity.setLabel("有新的异常指标");
                //                    entity.setHealthWarning(true);
                //                } else if (0 != anomalyIndexRepo.findByUidandDayBefore(entity.getUid(),
                //                        new DateTime(new Date()).plusMonths(-6).toString("yyyy-MM-dd")).size()) {
                //                    entity.setLabel("最近有异常指标");
                //                    entity.setHealthWarning(true);
                //                } else {
                entity.setLabel("最近无异常指标");
                //                }
                break;
            case 1:
                entity.setLabel("申请身份核实中");
                break;
            default:
                entity.setLabel("身份核实失败");
                break;
            }

            data.add(entity);
        }
        Integer todo = familyService.countTodo(uid);
        JsonListResponseEntity<FamilyMemberAPIEntity> body = new JsonListResponseEntity<>();
        body.setContent(data, false, null, null);
        if (memberId == null) {
            body.addExtra("invitation_count", String.valueOf(todo));
            body.addExtra("empty",
                    String.valueOf(familyMembers.size() == 0 && todo == 0 && familyService.countSent(uid) == 0));
        }
        return body;
    }

    /**
     * 解除情亲账户关系
     * @param uid
     * @param memberId
     * @return JsonResponseEntity<String>
     */
    @RequestMapping(value = "/member", method = RequestMethod.DELETE)
    @VersionRange
    public JsonResponseEntity<String> unbindRelation(@RequestParam String uid,
            @RequestParam("member_id") String memberId) {
        familyService.unbindFamilyRelation(uid, memberId);
        JsonResponseEntity<String> body = new JsonResponseEntity<>();
        body.setMsg("解除成功");
        return body;
    }

    /**
     * 修改备注名
     * @param request
     * @return JsonResponseEntity<Map<String,String>>
     */
    @RequestMapping(value = "/member/memo", method = RequestMethod.POST)
    @VersionRange
    public JsonResponseEntity<Map<String, String>> updateMemo(@RequestBody String request) {
        JsonKeyReader reader = new JsonKeyReader(request);
        String uid = reader.readString("uid", false);
        String memberId = reader.readString("member_id", false);
        String memo = reader.readDefaultString("memo", "");

        familyService.updateMemoName(uid, memberId, memo);
        JsonResponseEntity<Map<String, String>> body = new JsonResponseEntity<>();
        Map<String, String> data = ImmutableMap.of("memo", memo);
        body.setData(data);
        body.setMsg("修改成功");
        return body;
    }

    /**
     * 档案阅读权限开关
     * @param request
     * @return JsonResponseEntity<Map<String,Boolean>>
     */
    @RequestMapping(value = "/member/access/record", method = RequestMethod.POST)
    @VersionRange
    public JsonResponseEntity<Map<String, Boolean>> updateRecordReadAccess(@RequestBody String request) {
        JsonKeyReader reader = new JsonKeyReader(request);
        String uid = reader.readString("uid", false);
        String memberId = reader.readString("member_id", false);
        Boolean recordReadable = reader.readDefaultBoolean("record_readable", false);

        familyService.switchRecordReadAccess(uid, memberId, recordReadable);
        JsonResponseEntity<Map<String, Boolean>> body = new JsonResponseEntity<>();
        Map<String, Boolean> data = ImmutableMap.of("record_readable", recordReadable);
        body.setData(data);
        body.setMsg("修改成功");
        return body;
    }

}
