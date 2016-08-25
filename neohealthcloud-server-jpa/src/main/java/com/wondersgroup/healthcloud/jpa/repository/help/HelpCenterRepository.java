package com.wondersgroup.healthcloud.jpa.repository.help;

import com.wondersgroup.healthcloud.jpa.entity.help.HelpCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by shenbin on 16/8/12.
 */
public interface HelpCenterRepository extends JpaRepository<HelpCenter, String> {

    List<HelpCenter> findByIsVisable(String isVisable);

    @Modifying
    @Transactional
    @Query("delete from HelpCenter where id in ?1")
    void batchRemoveHelpCenter(List<String> ids);

    HelpCenter findById(String id);
}
