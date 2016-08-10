package com.wondersgroup.healthcloud.jpa.repository.dict;

import com.wondersgroup.healthcloud.jpa.entity.dic.Disease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by longshasha on 16/8/10.
 */
public interface DiseaseRepository  extends JpaRepository<Disease,String>{


    @Query(value = "select d from Disease d where d.isVisable='1' and d.isChronic='1' order by d.diseaseDesc")
    List<Disease> findAllDisease();

}
