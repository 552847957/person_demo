package com.wondersgroup.healthcloud.jpa.repository.user;

import com.wondersgroup.healthcloud.jpa.entity.user.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by longshasha on 16/8/10.
 */
public interface AddressRepository extends JpaRepository<Address,String> {

    @Query("select a from Address a where a.userId=?1 and a.delFlag='0'")
    List<Address> findByUserId(String userId);

    Address queryFirst1ByDelFlagAndUserId(String delFlag,String userId);

    @Query(nativeQuery = true, value = "SELECT a.* FROM app_tb_register_address a, app_tb_register_info b WHERE a.registerid = b.registerid AND b.del_flag = '0' AND b.personcard = ?1 order by b.update_date desc limit 0, 1")
    Address queryFirst1ByDelFlagAndPersoncard(String personcard);

}
