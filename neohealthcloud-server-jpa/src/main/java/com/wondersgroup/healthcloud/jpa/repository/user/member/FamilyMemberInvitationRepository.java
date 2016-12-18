package com.wondersgroup.healthcloud.jpa.repository.user.member;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.wondersgroup.healthcloud.jpa.entity.user.member.FamilyMemberInvitation;

public interface FamilyMemberInvitationRepository extends JpaRepository<FamilyMemberInvitation, String> {

    @Query("select fmi from FamilyMemberInvitation fmi where ((fmi.uid=?1 and fmi.memberId=?2) or (fmi.uid=?2 and fmi.memberId=?1)) and fmi.status!='2'")
    List<FamilyMemberInvitation> findByUserNotDenied(String userId, String memberId);

    @Query("select fmi from FamilyMemberInvitation fmi where ((fmi.uid=?1 and fmi.memberId=?2) or (fmi.uid=?2 and fmi.memberId=?1)) and fmi.status='1'")
    List<FamilyMemberInvitation> findByUserAccepted(String userId, String memberId);

    @Query("select fmi from FamilyMemberInvitation fmi where fmi.uid=?1 and fmi.memberId=?2")
    List<FamilyMemberInvitation> findByUser(String userId, String memberId);

    @Query("select fmi from FamilyMemberInvitation fmi where fmi.uid=?1 or fmi.memberId=?1 order by fmi.createDate desc")
    List<FamilyMemberInvitation> invitationList(String userId);
    
    @Query(nativeQuery = true, value = "select * from app_tb_family_member_invitation where (uid=?1 or member_id=?1) and status = '0'  order by create_date desc limit ?2")
    List<FamilyMemberInvitation> invitationList(String userId, int limit);
    
    @Query(nativeQuery = true, value = "select * from app_tb_family_member_invitation where uid=?1 and member_id=?2 and status = '0'  order by create_date desc limit 1")
    FamilyMemberInvitation invitation(String userId, String memberId, int limit);

    @Query("select count(1) from FamilyMemberInvitation fmi where fmi.memberId=?1 and fmi.status='0'")
    int countTodo(String userId);

    @Query("select count(1) from FamilyMemberInvitation fmi where fmi.uid=?1 and fmi.status='0'")
    int countSent(String userId);

}
