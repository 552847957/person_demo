package com.wondersgroup.healthcloud.jpa.repository.tag;

import com.wondersgroup.healthcloud.jpa.entity.tag.CTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by shenbin on 16/8/28.
 */
public interface CTagRepository extends JpaRepository<CTag, String> {

    @Transactional
    @Modifying
    @Query(value = "delete from CTag where id in ?1")
    void batchRemoveCTag(List<String> ids);

    CTag findById(String id);
}
