package com.wondersgroup.healthcloud.jpa.repository.config;

import com.wondersgroup.healthcloud.jpa.entity.config.AppConfig;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by zhaozhenxing on 2016/8/16.
 */
public interface AppConfigRepository extends JpaRepository<AppConfig, String> {

}
