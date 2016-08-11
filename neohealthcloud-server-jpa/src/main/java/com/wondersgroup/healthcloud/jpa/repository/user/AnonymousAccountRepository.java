package com.wondersgroup.healthcloud.jpa.repository.user;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.wondersgroup.healthcloud.jpa.entity.user.AnonymousAccount;

public interface AnonymousAccountRepository extends JpaRepository<AnonymousAccount, String> {
    @Query("select aa from AnonymousAccount aa where aa.idcard=?1")
    List<AnonymousAccount> findByIdcard(String idCard);

    @Query("select aa from AnonymousAccount aa where aa.creator=?1")
    List<AnonymousAccount> findByCreator(String creator);
}
