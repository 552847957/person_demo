package com.wondersgroup.healthcloud.services.config.impl;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.config.AppConfig;
import com.wondersgroup.healthcloud.jpa.repository.config.AppConfigRepository;
import com.wondersgroup.healthcloud.services.config.AppConfigService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaozhenxing on 2016/8/16.
 */
@Service("appConfigService")
public class AppConfigServiceImpl implements AppConfigService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AppConfigServiceImpl.class);

    @Autowired
    private AppConfigRepository appConfigRepository;

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jt;

    @Override
    public Map<String, String> findAppConfigByKeyWords(String mainArea, String specArea, String[] keyWords) {
        Map<String, String> cfgMap = null;
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT * FROM app_tb_neoconfiguration WHERE del_flag = '0'");
            if (!StringUtils.isEmpty(mainArea)) {
                sql.append(" AND main_area = '").append(mainArea).append("'");
            }
            if (!StringUtils.isEmpty(specArea)) {
                sql.append(" AND spec_area = '").append(specArea).append("'");
            }
            if (keyWords != null && keyWords.length > 0) {
                StringBuffer kws = new StringBuffer();
                for (int i = 0; i < keyWords.length; i++) {
                    if (!StringUtils.isEmpty(keyWords[i])) {
                        if (i == 0) {
                            kws.append("'").append(keyWords[i]).append("'");
                        } else {
                            kws.append(",'").append(keyWords[i]).append("'");
                        }
                    }
                }
                if (kws.length() > 0) {
                    sql.append(" AND key_word in (").append(kws.toString()).append(")");
                }
            }
            List<AppConfig> appConfigList = getJt().queryForList(sql.toString(), AppConfig.class);

            if (appConfigList != null && appConfigList.size() > 0) {
                cfgMap = new HashMap<>();
                for (AppConfig ac : appConfigList) {
                    cfgMap.put(ac.getKeyWord(), ac.getData());
                }
            }
        } catch (Exception ex) {
            logger.error("AppConfigServiceImpl.findAppConfigByKeyWord\t-->\t" + ex.getLocalizedMessage());
        }
        return cfgMap;
    }

    @Override
    public List<AppConfig> findAllDiscreteAppConfig(String mainArea, String specArea) {
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT * FROM app_tb_neoconfiguration WHERE del_flag = '0' AND discrete = 1");
            if (!StringUtils.isEmpty(mainArea)) {
                sql.append(" AND main_area = '").append(mainArea).append("'");
            }
            if (!StringUtils.isEmpty(specArea)) {
                sql.append(" AND spec_area = '").append(specArea).append("'");
            }

            List<AppConfig> appConfigList = getJt().queryForList(sql.toString(), AppConfig.class);

            if (appConfigList != null && appConfigList.size() > 0) {
                return appConfigList;
            }
        } catch (Exception ex) {
            logger.error("AppConfigServiceImpl.findAllDiscreteAppConfig\t-->\t" + ex.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public AppConfig findSingleAppConfigByKeyWord(String mainArea, String specArea, String keyWord) {
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT * FROM app_tb_neoconfiguration WHERE del_flag = '0'");
            if (!StringUtils.isEmpty(mainArea)) {
                sql.append(" AND main_area = '").append(mainArea).append("'");
            }
            if (!StringUtils.isEmpty(specArea)) {
                sql.append(" AND spec_area = '").append(specArea).append("'");
            }
            if (!StringUtils.isEmpty(keyWord)) {
                sql.append(" AND key_word = '").append(keyWord).append("'");
            }

            AppConfig appConfig = getJt().queryForObject(sql.toString(), AppConfig.class);

            if (appConfig != null) {
                return appConfig;
            }
        } catch (Exception ex) {
            logger.error("AppConfigServiceImpl.findSingleAppConfigByKeyWord\t-->\t" + ex.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public AppConfig saveAndUpdateAppConfig(AppConfig appConfig) {
        AppConfig rtnAppConfig = null;
        if (appConfig.getId() == null) {
            appConfig.setId(IdGen.uuid());
            appConfig.setCreateTime(new Date());
            appConfig.setDelFlag("0");
        }
        appConfig.setUpdateTime(new Date());
        try {
            rtnAppConfig = appConfigRepository.save(appConfig);
        } catch (Exception ex) {
            logger.error("AppConfigServiceImpl.saveAndUpdateAppConfig\t-->\t" + ex.getLocalizedMessage());
        }
        return rtnAppConfig;
    }

    /**
     * 获取jdbc template
     *
     * @return
     */
    private JdbcTemplate getJt() {
        if (jt == null) {
            jt = new JdbcTemplate(dataSource);
        }
        return jt;
    }
}
