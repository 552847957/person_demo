package com.wondersgroup.healthcloud.jpa.repository.friend;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wondersgroup.healthcloud.jpa.entity.friend.FriendInvite;

public interface FriendInviteRepository extends JpaRepository<FriendInvite, String> {

	FriendInvite findByMobileNum(String mobileNum);

	List<FriendInvite> findByUserIdAndStatus(String userId, int status);

}
