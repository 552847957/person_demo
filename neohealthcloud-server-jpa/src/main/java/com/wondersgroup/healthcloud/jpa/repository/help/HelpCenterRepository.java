package com.wondersgroup.healthcloud.jpa.repository.help;

import com.wondersgroup.healthcloud.jpa.entity.help.HelpCenter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by shenbin on 16/8/12.
 */
public interface HelpCenterRepository extends JpaRepository<HelpCenter, String> {

    List<HelpCenter> findByIsVisable(String isVisable);
}
