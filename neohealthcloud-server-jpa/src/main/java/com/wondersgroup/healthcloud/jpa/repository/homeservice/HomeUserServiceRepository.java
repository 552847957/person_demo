package com.wondersgroup.healthcloud.jpa.repository.homeservice;

import com.wondersgroup.healthcloud.jpa.entity.homeservice.HomeUserServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Administrator on 2017/5/9.
 */
public interface HomeUserServiceRepository extends JpaRepository<HomeUserServiceEntity,String> {
}
