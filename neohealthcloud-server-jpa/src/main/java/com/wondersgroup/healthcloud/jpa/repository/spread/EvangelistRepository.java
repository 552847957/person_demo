package com.wondersgroup.healthcloud.jpa.repository.spread;

import com.wondersgroup.healthcloud.jpa.entity.spread.Evangelist;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by nick on 2016/12/23.
 */
public interface EvangelistRepository extends JpaRepository<Evangelist, String> {

    Evangelist findBySpreadCode(String spreadCode);

    Evangelist findByStaffId(String staffId);
}
