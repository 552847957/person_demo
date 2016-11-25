package com.wondersgroup.healthcloud.jpa.repository.area;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondersgroup.healthcloud.jpa.entity.area.DicArea;

public interface DicAreaRepository extends JpaRepository<DicArea, String> {

    @Query(nativeQuery = true, value = " select * from t_dic_area where level =?1")
    List<DicArea> getAddressListByLevel(String level);

    @Query(nativeQuery = true, value = "select * from t_dic_area where upper_code =?1")
    List<DicArea> getAddressListByLevelAndFatherId(String upperCode);
    
    @Query(nativeQuery = true, value = "select * from t_dic_area where code =?1 limit 1")
    DicArea getAddress(String code);

}
