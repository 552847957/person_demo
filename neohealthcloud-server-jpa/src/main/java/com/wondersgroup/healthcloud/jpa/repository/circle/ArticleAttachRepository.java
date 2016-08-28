package com.wondersgroup.healthcloud.jpa.repository.circle;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondersgroup.healthcloud.jpa.entity.circle.ArticleAttach;

public interface ArticleAttachRepository extends JpaRepository<ArticleAttach,String>{

    @Query(value = "select a from ArticleAttach a where a.articleid=?1 and a.delFlag=0 order by a.sort asc")
    List<ArticleAttach> findByArticleid(String articleId);

}
