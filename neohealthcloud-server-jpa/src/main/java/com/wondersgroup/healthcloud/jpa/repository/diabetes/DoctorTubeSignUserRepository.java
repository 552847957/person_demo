package com.wondersgroup.healthcloud.jpa.repository.diabetes;

import com.wondersgroup.healthcloud.jpa.entity.diabetes.DoctorTubeSignUser;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by longshasha on 17/5/16.
 */
public interface DoctorTubeSignUserRepository extends JpaRepository<DoctorTubeSignUser, String>, JpaSpecificationExecutor<DoctorTubeSignUser> {

    @Query(nativeQuery = true, value = "select * from fam_doctor_tube_sign_user where card_number = ?1")
    public DoctorTubeSignUser queryInfoByCard(String card_number);

    @Modifying
    @Query(nativeQuery = true, value = "update fam_doctor_tube_sign_user set is_risk = ?2 where card_number = ?1 and card_type = '01'")
    public void updateRisk(String cardNumber, Integer isRisk);

    @Query("select a from DoctorTubeSignUser a where a.delFlag = ?1 and a.id in (?2)")
    Page<DoctorTubeSignUser> queryByDelFlagAndIdIn(String delFlag, List<String> ids, Pageable pageable);

//    @Query(nativeQuery = true, value = "select * from fam_doctor_tube_sign_user f where f.del_flag = 0 and f.name like %:kw% order by \n" +
//            "(case\n" +
//            "when f.name = :kw then 1 \n" +
//            "when f.name like :kw% then 2\n" +
//            "when f.name like %:kw then 3\n" +
//            "when f.name like %:kw% then 4  \n" +
//            "else 0\n" +
//            "end ) limit :start,:pageSize")
//    List<DoctorTubeSignUser> searchByKw(@Param("kw") String kw, @Param("start") int start, @Param("pageSize") int pageSize);
}
