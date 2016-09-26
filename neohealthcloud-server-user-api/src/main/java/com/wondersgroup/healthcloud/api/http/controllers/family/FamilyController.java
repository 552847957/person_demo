package com.wondersgroup.healthcloud.api.http.controllers.family;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.wondersgroup.common.http.builder.RequestBuilder;
import com.wondersgroup.common.http.entity.JsonNodeResponseWrapper;
import com.wondersgroup.common.image.utils.ImagePath;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.session.AccessToken;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
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
import com.wondersgroup.healthcloud.services.user.exception.ErrorChangeMobileException;
import com.wondersgroup.healthcloud.services.user.exception.ErrorChildVerificationException;
import com.wondersgroup.healthcloud.utils.DateFormatter;
import com.wondersgroup.healthcloud.utils.IdcardUtils;

/**
 * 孫海迪
 * Created by sunhaidi on 2016.8.10
 */

@RestController
@RequestMapping("/api/family")
public class FamilyController {

    private static final Logger logger = LoggerFactory.getLogger(FamilyController.class);

    @Autowired
    private UserAccountService      accountService;

    @Autowired
    private UserService             userService;

    @Autowired
    private FamilyService           familyService;

    @Autowired
    private AnonymousAccountService anonymousAccountService;

    @Autowired
    private Environment environment;

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
                body.setCode(1004);
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
            if (accountService.checkAccount(mobile)) {
                RegisterInfo register = accountService.fetchInfo(mobile);
                data.put("avatar", register.getHeadphoto() + ImagePath.avatarPostfix());
                data.put("nickname", register.getNickname());
            } else {
                throw new ErrorChangeMobileException(1004, "无相关账户");
            }
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
    public JsonResponseEntity<String> helpRegistration(@RequestBody String request) {
        JsonKeyReader reader = new JsonKeyReader(request);
        String uid = reader.readString("uid", false);
        String mobile = reader.readString("mobile", false);
        String verifyCode = reader.readString("verify_code", false);
        String password = reader.readString("password", false);
        String relation = reader.readString("relation", false);
        String relationName = reader.readString("relation_name", true);
        String path = reader.readString("avatar", true);
        Boolean recordReadable = reader.readDefaultBoolean("record_readable", true);
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
    public JsonResponseEntity<String> registrationCode(
            @Header(name = "spec-area",defaultValue = "") String area, 
            @RequestParam String uid, 
            @RequestParam String mobile,
            @RequestParam String relation, 
            @RequestParam("relation_name") String relationName) {
        familyService.sendRegistrationCode(uid, relation, relationName, mobile,area);
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
    public JsonResponseEntity<String> agreeInvitation(@RequestBody String request, @AccessToken Session session) {
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
                entity.setRedirectFlag(0);
                JsonNode submitInfo = accountService.verficationSubmitInfo(anonymousAccount.getId(), true);
                if(submitInfo != null){
                    Integer status = submitInfo.get("status").asInt();
                    String  name =  submitInfo.get("name").asText();
                    String  idcard = submitInfo.get("idcard").asText();
                    if (!anonymousAccount.getIsChild() && anonymousAccount.getIdcard() == null) {
                        entity.setRedirectFlag(status - 1);
                    }else{
                        if(status == 2){ status = 2;
                        }else if(status == 1){ status = 3;
                        }else{ status = 4; }
                        
                        entity.setRedirectFlag(status);
                        entity.setName(IdcardUtils.cardNameYard(name));
                        entity.setIdCard(IdcardUtils.cardYard(idcard));
                    }
                }
            }
            entity.setLabelColor("#666666");
            entity.setHealthWarning(false);
            switch (entity.getRedirectFlag()) {
            case 0:
                if (haveMeasureException(entity.getUid(), DateFormatter.dateFormat(new Date()), 2)) {
                    entity.setLabel("有新的异常指标");
                    entity.setHealthWarning(true);
                    entity.setLabelColor("#CC0000");
                } else if (haveMeasureException(entity.getUid(), new DateTime(new Date()).plusMonths(-6).toString("yyyy-MM-dd"), 1)) {
                    entity.setLabel("最近有异常指标");
                    entity.setHealthWarning(true);
                    entity.setLabelColor("#CC0000");
                } else {
                    entity.setLabel("最近无异常指标");
                }
                break;
            case 1:
                entity.setLabel("申请身份核实中");
                break;
            case 2:
                entity.setLabel("实名制认证审核中");
                break;
            case 3:
                entity.setLabel("已通过实名制认证");
                break;
            case 4:
                entity.setLabel("实名制认证失败");
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
     * 实名认证，对方无手机号 点此为其认证
     * @param uid
     * @param relation
     * @param relationName
     * @param name
     * @param idcard
     * @param photo
     * @return JsonResponseEntity<String>
     */
    @RequestMapping(value = "/member/registration/anonym", method = RequestMethod.POST)
    @VersionRange
    public JsonResponseEntity<String> anonymousRegistration(
            @RequestBody String request) {
        
        JsonKeyReader reader = new JsonKeyReader(request);
        String uid = reader.readString("uid", false);
        String relation = reader.readString("relation", false);
        String relationName = reader.readString("relation_name",true);
        String name = reader.readString("name", false);
        String idcard = reader.readString("idcard", false);
        String photo = reader.readString("photo", false);
        JsonResponseEntity<String> body = new JsonResponseEntity<>();
        familyService.anonymousRegistration(uid, relation, relationName, name, idcard, photo);
        body.setMsg("添加成功, 正在进行实名认证");
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

        JsonResponseEntity<Map<String, String>> body = new JsonResponseEntity<>();
        Map<String, String> data = ImmutableMap.of("memo", memo);
        FamilyMember familyMember = familyService.getFamilyMemberWithOrder(uid, memberId);
        if(familyMember == null || familyMember.getId() == null){
            body.setCode(1695);
            body.setData(data);
            body.setMsg("用户已解绑，修改失败");
            return body;
        }
        familyService.updateMemoName(uid, memberId, memo);
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

    /**
     * 是否有异常指标
     * @param registerId
     * @param date
     * @param isNew  1 最近是否有，2 是否有新的
     * @return boolean
     */
    public boolean haveMeasureException(String registerId, String date, int isNew) {
        boolean res = false;
        try {
            String url = environment.getProperty("internal.api.service.measure.url");
            url += isNew == 1 ? "/api/measure/abnormal/afterDate" : "/api/measure/abnormal/byDate";
            String[] header = new String[] { "version", "3.0" };
            String[] form = new String[] { "registerId", registerId, "date", date};
            OkHttpClient client = new OkHttpClient();
            HttpRequestExecutorManager httpRequestExecutorManager = new HttpRequestExecutorManager(client);
            Request request = new RequestBuilder().get().url(url).params(form).headers(header).build();
            JsonNodeResponseWrapper response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(request).run()
                    .as(JsonNodeResponseWrapper.class);
            JsonNode result = response.convertBody();

            if (result.get("code").asInt() == 0 && result.get("data").get("hashAbnormal").asBoolean()) {
                res = true;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        
        return res;
    }
    
    /**
     * 提交儿童实名认证信息
     * @return
     */
    @VersionRange
    @PostMapping(path = "/childVerification/submit")
    public JsonResponseEntity<String> verificationSubmit(@RequestBody String request) {
        JsonKeyReader reader = new JsonKeyReader(request);
        String id = reader.readString("uid", false);//监护人Id
        String name = reader.readString("name", false);//儿童的真实姓名
        String idCard = reader.readString("idcard", false);//儿童的身份证号
        String idCardFile = reader.readString("idCardFile", false);//户口本(儿童身份信息页照片)
        String birthCertFile = reader.readString("birthCertFile", false);//出生证明(照片)
        JsonResponseEntity<String> body = new JsonResponseEntity<>();
        name = name.trim();
        idCard = idCard.trim();
        int age = IdcardUtils.getAgeByIdCard(idCard);
        if(age >= 18){
            throw new ErrorChildVerificationException("年龄大于等于18岁的不能使用儿童实名认证");
        }
        
        Boolean result = familyService.childVerificationRegistration(id, name, idCard, idCardFile, birthCertFile);
        if(!result){
            body.setCode(1001);
            body.setMsg("提交失败");
            return body;
        }
        body.setMsg("实名认证已提交，请耐心等待");
        return body;
    }
    
    /**
     * 亲情账户儿童实名认证是否打开
     * @return JsonResponseEntity<String>
     */
    @VersionRange
    @GetMapping(path = "/isOpenVerification")
    public JsonResponseEntity<Map<String, String>> openVerification(@RequestParam() String uid){
        RegisterInfo register = userService.getOneNotNull(uid);
        JsonResponseEntity<Map<String, String>> result = new JsonResponseEntity<Map<String, String>>();
        Map<String, String> map = new HashMap<String, String>();
        String identifytype = register.getIdentifytype();
        map.put("identifyType", identifytype);
        result.setData(map);
        result.setMsg("查询成功");
        return result;
    }

}
