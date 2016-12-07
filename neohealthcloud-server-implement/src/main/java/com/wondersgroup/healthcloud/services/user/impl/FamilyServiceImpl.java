package com.wondersgroup.healthcloud.services.user.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.wondersgroup.common.http.utils.JsonConverter;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.helper.family.FamilyMemberAccess;
import com.wondersgroup.healthcloud.helper.family.FamilyMemberRelation;
import com.wondersgroup.healthcloud.helper.push.api.AppMessage;
import com.wondersgroup.healthcloud.helper.push.api.AppMessageUrlUtil;
import com.wondersgroup.healthcloud.helper.push.api.PushClientWrapper;
import com.wondersgroup.healthcloud.jpa.entity.user.AnonymousAccount;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.entity.user.member.FamilyMember;
import com.wondersgroup.healthcloud.jpa.entity.user.member.FamilyMemberInvitation;
import com.wondersgroup.healthcloud.jpa.repository.user.RegisterInfoRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.member.FamilyMemberInvitationRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.member.FamilyMemberRepository;
import com.wondersgroup.healthcloud.services.user.FamilyService;
import com.wondersgroup.healthcloud.services.user.UserAccountService;
import com.wondersgroup.healthcloud.services.user.UserService;
import com.wondersgroup.healthcloud.services.user.exception.ErrorChangeMobileException;
import com.wondersgroup.healthcloud.services.user.exception.ErrorChildVerificationException;
import com.wondersgroup.healthcloud.utils.IdcardUtils;
import com.wondersgroup.healthcloud.utils.wonderCloud.AccessToken;
import com.wondersgroup.healthcloud.utils.wonderCloud.HttpWdUtils;

@Service
public class FamilyServiceImpl implements FamilyService {
    private static final int                 maxMemberCount = 10;

    @Autowired
    private Environment                      environment;

    @Autowired
    private RegisterInfoRepository           registerRepository;

    @Autowired
    private FamilyMemberRepository           memberRepository;

    @Autowired
    private FamilyMemberInvitationRepository invitationRepository;

    @Autowired
    private UserAccountService               accountService;

    @Autowired
    private UserService                      userService;
    
    @Autowired
    private HttpWdUtils httpWdUtils;
    
    @Autowired
    private HttpRequestExecutorManager httpRequestExecutorManager;
    
    @Autowired
    private PushClientWrapper pushClientWrapper;
    
    @Transactional(readOnly = false)
    @Override
    public Boolean inviteMember(String userId, String memberId, String mobile, String memo, String relation,
            String relationName, Boolean recordReadable) {
        RegisterInfo register = findOneRegister(userId, false);//找到申请者
        if (register.getGender() == null) {
            throw new ErrorChangeMobileException(1058, "请先输入本人性别");
        }
        checkMemberCount(userId);

        RegisterInfo other = memberId == null ? findOneRegisterByMobile(mobile, false) : findOneRegister(memberId,
                false);//找到被邀请人, 如果有memberId, 则以memberId为准
        if (userId.equals(other.getRegisterid())) {
            throw new ErrorChangeMobileException(1053, "不能添加自己为家庭成员");
        }
        memberId = other.getRegisterid();
        checkMemberCount(memberId);

        List<FamilyMemberInvitation> invitationInStore = invitationRepository.findByUserNotDenied(userId,
                other.getRegisterid());//找到两人间未处理的申请
        if (invitationInStore.size() > 0) {
            if ("0".equals(invitationInStore.get(0).getStatus())) {
                throw new ErrorChangeMobileException(1051, "已存在邀请, 不能重复邀请");
            } else {
                throw new ErrorChangeMobileException(1050, "你们已经建立家庭成员关联");
            }
        }

        List<FamilyMember> exist = memberRepository.findByTwoUser(userId, memberId);
        if (!exist.isEmpty()) {
            throw new ErrorChangeMobileException(1053, "已经是家庭成员, 不能重复添加");
        }

        FamilyMemberInvitation invitation = new FamilyMemberInvitation();
        invitation.setId(IdGen.uuid());
        invitation.setUid(userId);
        invitation.setMemberId(other.getRegisterid());
        invitation.setRelation(relation);
        invitation.setRelationName(FamilyMemberRelation.isOther(relation) ? relationName : FamilyMemberRelation
                .getName(relation));
        FamilyMemberAccess.Builder builder = new FamilyMemberAccess.Builder();
        builder.recordAccess(recordReadable, false);
        invitation.setAccess(builder.build());
        invitation.setMemo(memo != null ? memo : relationName);
        invitation.setStatus("0");
        invitation.setDelFlag("0");
        invitation.setCreateBy(userId);
        invitation.setCreateDate(new Date());
        invitation.setUpdateBy(userId);
        invitation.setUpdateDate(invitation.getCreateDate());
        invitationRepository.saveAndFlush(invitation);
        push(other.getRegisterid(), "家庭成员邀请", "您收到一条家庭成员邀请, 请查收");
        return true;
    }

    @Transactional(readOnly = false)
    @Override
    public Boolean helpRegistration(String userId, String mobile, String password, String verifyCode, String avatar,
            String memo, String relation, String relationName, Boolean recordReadable) {
        checkMemberCount(userId);
        AccessToken accessToken = accountService.register(mobile, verifyCode, password);
        userService.updateAvatar(accessToken.getUid(), avatar);
        RegisterInfo register = findOneRegister(userId, false);
        createMemberRelationPair(
                userId,
                accessToken.getUid(),
                relation,
                register.getGender(),
                relationName,
                FamilyMemberRelation.isOther(relation) ? null : FamilyMemberRelation.getName(FamilyMemberRelation
                        .getOppositeRelation(relation, register.getGender())), recordReadable, true, false);

        return true;
    }

    @Transactional(readOnly = false)
    @Override
    public Boolean dealWithMemberInvitation(String invitationId, String userId, Boolean agree, String relationName,
            Boolean recordReadable) {
        FamilyMemberInvitation invitation = invitationRepository.findOne(invitationId);

        if (agree) {
            checkMemberCount(userId);
            checkMemberCount(invitation.getUid());
        }

        if (!StringUtils.equals(userId, invitation.getMemberId())) {
            throw new ErrorChangeMobileException(1000, "只有被邀请人能通过");
        }
        if (!StringUtils.equals("0", invitation.getStatus())) {
            throw new ErrorChangeMobileException(1053, "邀请已被处理, 不能再次处理");
        }
        List<FamilyMember> exist = memberRepository.findByTwoUser(invitation.getUid(), invitation.getMemberId());
        if (!exist.isEmpty()) {
            throw new ErrorChangeMobileException(1053, "已经是家庭成员, 不能重复添加");
        }

        invitation.setStatus(agree ? "1" : "2");
        invitation.setUpdateBy(invitation.getMemberId());
        invitation.setUpdateDate(new Date());
        invitationRepository.saveAndFlush(invitation);
        if (agree) {
            RegisterInfo register = findOneRegister(invitation.getUid(), false);
            createMemberRelationPair(invitation.getUid(), invitation.getMemberId(), invitation.getRelation(),
                    register.getGender(), invitation.getRelationName(), relationName,
                    FamilyMemberAccess.recordReadable(invitation.getAccess()), recordReadable, false);
        }
//        push(invitation.getUid(), "家庭成员邀请", "您的一条家庭成员邀请已被处理, 请查收");
        return true;
    }

    @Transactional(readOnly = false)
    @Override
    public String createMemberRelationPair(String user1Id, String user2Id, String relation, String gender,
            String relationName1, String relationName2, Boolean recordReadable1, Boolean recordReadable2,
            Boolean isAnonymous) {
        Date time = new Date();
        String pairId = IdGen.uuid();
        FamilyMember familyMember1 = new FamilyMember();
        familyMember1.setId(IdGen.uuid());
        familyMember1.setPairId(pairId);
        familyMember1.setUid(user1Id);
        familyMember1.setMemberId(user2Id);
        familyMember1.setRelation(relation);
        familyMember1.setRelationName(relationName1);
        familyMember1.setMemo(FamilyMemberRelation.isOther(relation) ? relationName1 : null);
        FamilyMemberAccess.Builder builder = new FamilyMemberAccess.Builder();
        builder.recordAccess(recordReadable1, false);
        familyMember1.setAccess(builder.build());
        familyMember1.setCreateBy(user1Id);
        familyMember1.setCreateDate(time);
        familyMember1.setUpdateBy(familyMember1.getCreateBy());
        familyMember1.setUpdateDate(familyMember1.getCreateDate());
        familyMember1.setIsAnonymous(isAnonymous ? 1 : 0);
        familyMember1.setDelFlag("0");
        familyMember1.setStatus("1");
        memberRepository.saveAndFlush(familyMember1);

        FamilyMember familyMember2 = new FamilyMember();
        familyMember2.setId(IdGen.uuid());
        familyMember2.setPairId(pairId);
        familyMember2.setUid(familyMember1.getMemberId());
        familyMember2.setMemberId(familyMember1.getUid());
        familyMember2.setRelation(FamilyMemberRelation.getOppositeRelation(relation, gender));
        familyMember2.setRelationName(FamilyMemberRelation.getName(familyMember2.getRelation(), relationName2));
        familyMember2.setMemo(FamilyMemberRelation.isOther(relation) ? relationName2 : null);
        FamilyMemberAccess.Builder newBuilder = new FamilyMemberAccess.Builder();
        newBuilder.recordAccess(recordReadable2, false);
        familyMember2.setAccess(newBuilder.build());
        familyMember2.setCreateBy(user2Id);
        familyMember2.setCreateDate(time);
        familyMember2.setUpdateBy(familyMember2.getCreateBy());
        familyMember2.setUpdateDate(familyMember2.getUpdateDate());
        familyMember2.setIsAnonymous(0);
        familyMember2.setDelFlag("0");
        familyMember2.setStatus("1");
        memberRepository.saveAndFlush(familyMember2);

        return pairId;
    }

    @Transactional(readOnly = false)
    @Override
    public Boolean updateMemoName(String userId, String memberId, String memoName) {
        FamilyMember familyMember = memberRepository.findRelationWithOrder(userId, memberId);
        familyMember.setMemo(memoName);
        familyMember.setUpdateBy(userId);
        familyMember.setUpdateDate(new Date());
        memberRepository.saveAndFlush(familyMember);
        return true;
    }

    @Transactional(readOnly = false)
    @Override
    public Boolean unbindFamilyRelation(String userId, String memberId) {
        List<FamilyMember> familyMembers = memberRepository.findByTwoUser(userId, memberId);
        memberRepository.delete(familyMembers);
        List<FamilyMemberInvitation> familyMemberInvitations = invitationRepository
                .findByUserAccepted(userId, memberId);//将邀请表里的状态改为拒绝, 已保证可以再次邀请
        for (FamilyMemberInvitation familyMemberInvitation : familyMemberInvitations) {
            familyMemberInvitation.setStatus("2");
            invitationRepository.save(familyMemberInvitation);
        }
        RegisterInfo register = findOneRegister(userId, false);
//        String message = register.getNickname() + "已与您解除亲情账户绑定";
//        push(memberId, "亲情账户解除绑定", message);
        return true;
    }

    @Transactional(readOnly = false)
    @Override
    public Boolean switchRecordReadAccess(String userId, String memberId, Boolean readReadable) {
        FamilyMember familyMember = memberRepository.findRelationWithOrder(userId, memberId);
        FamilyMemberAccess.Builder builder = new FamilyMemberAccess.Builder(familyMember.getAccess());
        builder.recordAccess(readReadable, false);
        familyMember.setAccess(builder.build());
        familyMember.setUpdateBy(userId);
        familyMember.setUpdateDate(new Date());
        memberRepository.saveAndFlush(familyMember);
        return true;
    }

    @Override
    public Boolean canReadRecord(String userId, String memberId) {
        FamilyMember familyMember = memberRepository.findRelationWithOrder(memberId, userId);//查看对方对我的权限设定
        return familyMember != null && FamilyMemberAccess.recordReadable(familyMember.getAccess());
    }

    @Override
    public List<FamilyMemberInvitation> getInvitationList(String userId) {
        return invitationRepository.invitationList(userId);
    }

    @Override
    public int countTodo(String userId) {
        return invitationRepository.countTodo(userId);
    }

    @Override
    public int countSent(String userId) {
        return invitationRepository.countSent(userId);
    }

    @Override
    public List<FamilyMember> getFamilyMembers(String userId) {
        return memberRepository.members(userId);
    }

    @Override
    public FamilyMember getFamilyMemberWithOrder(String userId, String memberId) {
        return memberRepository.findRelationWithOrder(userId, memberId);
    }

    @Override
    public Boolean sendRegistrationCode(String uid, String relation, String relationName, String mobile) {
        return sendRegistrationCode(uid, relation, relationName, mobile, null);
    }
    
    @Override
    public Boolean sendRegistrationCode(String uid, String relation, String relationName, String mobile, String area) {
        RegisterInfo register = findOneRegister(uid, false);
        String mobileMessage = register.getRegmobilephone() == null ? "" : String.format("（尾号%s）",
                StringUtils.substring(register.getRegmobilephone(), 7));
        String message = "验证码：:code。您的"
                + (StringUtils.equals("0", relation) ? "家人" : FamilyMemberRelation.getName(FamilyMemberRelation
                        .getOppositeRelation(relation, register.getGender()))) + mobileMessage;
        if("4401".equals(area)){
            message += "为您创建了广州健康通账户，以便于更好的管理您的家人健康。";
        }else{
            message += "为您创建了健康云账户，以便于更好的管理您的家人健康。请点击http://www.wdjky.com/healthcloud2 进行APP下载。";
        }
        JsonNode node = httpWdUtils.sendCode(mobile, message);
        return node.get("success").asBoolean();
    }

    @Transactional(readOnly = false)
    @Override
    public void anonymousRegistration(String userId, String relation, String relationName, String name, String idcard,
            String photo) {
        checkMemberCount(userId);
        RegisterInfo register = findOneRegister(userId, false);
        AnonymousAccount account = accountService.anonymousRegistration(userId, "HCGEN" + IdGen.uuid(), IdGen.uuid());
        createMemberRelationPair(userId, account.getId(), relation, register.getGender(), relationName, FamilyMemberRelation.isOther(relation) ? null : FamilyMemberRelation.getName(FamilyMemberRelation.getOppositeRelation(relation, register.getGender())), true, true, true);
        accountService.verificationSubmit(account.getId(), name, idcard, photo);
    }
    
    @Transactional(readOnly = false)
    @Override
    public Boolean childVerificationRegistration(String userId,String name, String idCard, String idCardFile, String birthCertFile) {
        String gender = IdcardUtils.getGenderByIdCard(idCard);
        String relation = "1".equals(gender) ? "4" : "5";//4 儿子 5 女儿
        String relationName = "1".equals(gender) ? "儿子" : "女儿";
        
        checkMemberCount(userId);
        RegisterInfo register = findOneRegister(userId, false);
        if(!"1".equals(register.getIdentifytype()) || StringUtils.isEmpty(register.getRegmobilephone())){
            throw new ErrorChildVerificationException("非实名认证和未绑定手机号的用户不能添加儿童实名认证");
        }
        AnonymousAccount account = accountService.childVerificationRegistration(userId, "HCGEN" + IdGen.uuid(), IdGen.uuid());
        boolean result = accountService.childVerificationSubmit(userId, account.getId(), name, idCard, idCardFile, birthCertFile);
        createMemberRelationPair(userId, account.getId(), relation, register.getGender(), relationName, FamilyMemberRelation.isOther(relation) ? null : FamilyMemberRelation.getName(FamilyMemberRelation.getOppositeRelation(relation, register.getGender())), true, true, true);
        return  result;
    }

    private List<FamilyMember> findByTwoUser(String userId, String memberId, Boolean nullable) {
        List<FamilyMember> exist = memberRepository.findByTwoUser(userId, memberId);
        if (nullable || exist.isEmpty()) {
            throw new ErrorChangeMobileException(1052, "双方不是家庭成员关系");
        }
        return exist;
    }

    private Boolean push(String userId, String title, String content) {
        boolean result = false;
        try {
            AppMessage message = AppMessage.Builder.init().title(title).content(content).type(AppMessageUrlUtil.Type.FAMILY).urlFragment(AppMessageUrlUtil.familyInvitation()).build();
            result = pushClientWrapper.pushToAlias(message, userId);
        } catch (Exception e) {
            return false;
        }
        return result;
    }

    private RegisterInfo findOneRegister(String id, Boolean nullable) {
        RegisterInfo register = registerRepository.findOne(id);
        if (register != null || nullable) {
            return register;
        } else {
            throw new ErrorChangeMobileException(1001, "不存在的用户");
        }
    }

    private RegisterInfo findOneRegisterByMobile(String mobile, Boolean nullable) {
        RegisterInfo register = registerRepository.findByMobile(mobile);
        if (register != null || nullable) {
            return register;
        } else {
            throw new ErrorChangeMobileException(1004, "该手机号码还未注册，对方要先注册账号");
        }
    }

    private void checkMemberCount(String userId) {
        if (memberRepository.familyMemberCount(userId) >= maxMemberCount) {
            throw new ErrorChangeMobileException(1059, "本人或对方已添加五个亲情账户");
        }
    }

}
