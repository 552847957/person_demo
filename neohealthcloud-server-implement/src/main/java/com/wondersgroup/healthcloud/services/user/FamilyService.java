package com.wondersgroup.healthcloud.services.user;

import java.util.List;

import com.wondersgroup.healthcloud.jpa.entity.user.member.FamilyMember;
import com.wondersgroup.healthcloud.jpa.entity.user.member.FamilyMemberInvitation;

public interface FamilyService {

    /**
     * 邀请一个用户成为家庭成员
     *
     * @param userId         用户id
     * @param memberId       被邀请的用户id
     * @param mobile         被邀请的用户手机号(与memberId互斥, 优先考虑memberId)
     * @param memo           备注名
     * @param relation       关系
     * @param relationName   关系名(当relation=0时起效)
     * @param recordReadable 对方对自己档案的可读性
     * @return Map<Integer, String> code，msg
     */
    Boolean inviteMember(String userId, String memberId, String mobile, String memo, String relation, String relationName, Boolean recordReadable);

    /**
     * 帮助注册接口
     *
     * @param userId         用户id
     * @param mobile         对方手机号
     * @param password       对方密码
     * @param verifyCode     对方手机验证码
     * @param avatar         对方头像
     * @param memo           备注名
     * @param relation       关系
     * @param relationName   关系名(当relation=0时起效)
     * @param recordReadable 对方对自己档案的可读性
     * @return
     */
    Boolean helpRegistration(String userId, String mobile, String password, String verifyCode, String avatar, String memo, String relation, String relationName, Boolean recordReadable);

    /**
     * 处理亲情账户邀请
     *
     * @param invitationId   邀请id
     * @param userId         用户id
     * @param agree          是否同意
     * @param relationName   对方的关系(若对方选择relation=0则起效)
     * @param recordReadable 对方对自己档案的可读性
     * @return
     */
    Boolean dealWithMemberInvitation(String invitationId, String userId, Boolean agree, String relationName, Boolean recordReadable);

    /**
     * 更新备注名
     *
     * @param userId
     * @param memberId
     * @param memoName
     * @return
     */
    Boolean updateMemoName(String userId, String memberId, String memoName);

    /**
     * 亲情账户关系解绑
     *
     * @param userId   用户id
     * @param memberId 对方id
     * @return
     */
    Boolean unbindFamilyRelation(String userId, String memberId);

    /**
     * 切换对方对本人档案的可读性
     *
     * @param userId       用户id
     * @param memberId     对方id
     * @param readReadable 是否可读
     * @return
     */
    Boolean switchRecordReadAccess(String userId, String memberId, Boolean readReadable);

    /**
     * 判断本人是否可以读取对方的健康档案
     *
     * @param userId   用户id
     * @param memberId 对方id
     * @return
     */
    Boolean canReadRecord(String userId, String memberId);

    /**
     * 得到自己发出或收到的亲情账户邀请列表
     *
     * @param userId
     * @return
     */
    List<FamilyMemberInvitation> getInvitationList(String userId);

    /**
     * 得到待处理的亲情账户相关消息数(包含收到的亲情账户邀请)
     *
     * @param userId
     * @return
     */
    int countTodo(String userId);

    /**
     * 得到本人发出的亲情账户邀请数
     *
     * @param userId
     * @return
     */
    int countSent(String userId);

    /**
     * 得到亲情账户列表
     *
     * @param userId
     * @return
     */
    List<FamilyMember> getFamilyMembers(String userId);

    /**
     * 得到一条亲情账户的数据
     * 由于亲情账户在数据库中存有两条数据, 本方法得到某人对另一人的关系数据
     *
     * @param userId
     * @param memberId
     * @return
     */
    FamilyMember getFamilyMemberWithOrder(String userId, String memberId);

    /**
     * 发送帮助注册短信验证码, 详见helpRegistration接口
     *
     * @param uid
     * @param relation
     * @param relationName
     * @param mobile
     * @return
     */
    Boolean sendRegistrationCode(String uid, String relation, String relationName, String mobile);

    /**
     * 匿名注册
     *
     * @param userId
     * @param relation
     * @param relationName
     * @param name
     * @param idcard
     * @param photo
     */
    void anonymousRegistration(String userId, String relation, String relationName, String name, String idcard, String photo);
}
