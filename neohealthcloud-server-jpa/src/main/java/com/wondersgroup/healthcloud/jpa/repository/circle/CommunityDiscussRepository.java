package com.wondersgroup.healthcloud.jpa.repository.circle;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondersgroup.healthcloud.jpa.entity.circle.CommunityDiscuss;

public interface CommunityDiscussRepository extends JpaRepository<CommunityDiscuss,String>{

    @Query(value = "select c from CommunityDiscuss c where c.articleid=?1 and c.delFlag='0' and c.discusstime<?2 ")
    Page<CommunityDiscuss> findByArticleIdWithFlag(String articleId,Date flag,Pageable pageable);

    @Query(value = "select c from CommunityDiscuss c where c.articleid=?1 and c.delFlag='0' ")
    Page<CommunityDiscuss> findByArticleId(String articleId,Pageable pageable);

    @Query(value = "select c from CommunityDiscuss c where c.registerid=?1 and c.delFlag='0' and c.id=?2 ")
    CommunityDiscuss findUserAndCommentId(String registerId,String commentId);
}
