package com.wondersgroup.healthcloud.jpa.repository.user.member;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.wondersgroup.healthcloud.jpa.entity.user.member.FamilyMember;

public interface FamilyMemberRepository extends JpaRepository<FamilyMember, String> {
    @Query("select fm from FamilyMember fm where (fm.uid=?1 and fm.memberId=?2) or (fm.uid=?2 and fm.memberId=?1)")
    List<FamilyMember> findByTwoUser(String user1, String user2);

    @Query("select fm from FamilyMember fm where fm.uid=?1 and fm.memberId=?2")
    FamilyMember findRelationWithOrder(String userId, String memberId);

    @Query("select fm from FamilyMember fm where fm.pairId=?1")
    List<FamilyMember> findByPairId(String pairId);

    @Query("select fm from FamilyMember fm where fm.uid=?1 order by fm.order,fm.createDate")
    List<FamilyMember> members(String userId);

    @Query("select count(1) from FamilyMember fm where fm.uid=?1")
    Integer familyMemberCount(String userId);
}
