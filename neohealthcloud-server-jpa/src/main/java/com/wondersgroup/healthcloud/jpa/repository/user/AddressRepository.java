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

}
