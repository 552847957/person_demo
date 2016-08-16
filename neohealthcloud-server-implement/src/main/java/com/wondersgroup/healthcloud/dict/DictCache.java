/**
 *
 */
package com.wondersgroup.healthcloud.dict;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 字典工具类, 包含获取与缓存功能
 *
 * @author zhangzhixiu
 */
@Component
public class DictCache {
    protected static final Logger logger = LoggerFactory.getLogger(DictCache.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jt;

    private LoadingCache<String, Map<String, String>> caches = CacheBuilder.newBuilder().maximumSize(10).expireAfterWrite(1, TimeUnit.DAYS).build(new CacheLoader<String, Map<String, String>>() {
        @Override
        public Map<String, String> load(String key) throws Exception {
            logger.info("Dictionary cache fetch data by key: " + key);
            String[] s = key.split(":");
            String codeColName = s[1];
            String meaningColName = s[2];
            String dictTable = s[0];
            Map<String, String> map = Maps.newConcurrentMap();
            List<Map<String, Object>> result = jt.queryForList(String.format("select %s as code, %s as meaning from %s", codeColName, meaningColName, dictTable));
            for (Map<String, Object> row : result) {
                map.put((String) row.get("code"), (String) row.get("meaning"));
            }

            return map;
        }
    });


    /**
     * 获取标签名
     *
     * @param tagId 标签id
     * @return
     */
    public String queryTagName(String tagId) {
        if(StringUtils.isNotEmpty(tagId)) {
            try {
                return caches.get("app_dic_tag:id:tagname").get(tagId);
            } catch (ExecutionException ex) {
                return "";
            }
        }
        return "";
    }

    /**
     * 获取标签颜色
     *
     * @param tagId 标签id
     * @return
     */
    public String queryTagColor(String tagId) {
        if(StringUtils.isNotEmpty(tagId)) {
            try {
                return caches.get("app_dic_tag:id:tagcolor").get(tagId);
            } catch (ExecutionException ex) {
                return "";
            }
        }
        return "";
    }

    /**
     * 获取医院代码含义
     *
     * @param code 医院代码
     * @return
     * @deprecated
     */
    public String queryHospitalName(String code) {
        try {
            return caches.get("t_dic_hospital_info:hospital_id:hospital_name").get(code);
        } catch (ExecutionException ex) {
            return "";
        }
    }

    /**
     * 获取检查项目代码含义
     *
     * @param code 检查项目含义
     * @return
     */
    public String queryCheckItem(String code) {
        try {
            return caches.get("t_dic_diabetes_check:item_code:explain_memo").get(code);
        } catch (ExecutionException ex) {
            return "";
        }
    }

    /**
     * 取地址代码含义
     *
     * @param code
     * @return
     */
    public String queryArea(String code) {
        try {
            return caches.get("t_dic_area:code:explain_memo").get(StringUtils.defaultString(code));
        } catch (ExecutionException ex) {
            return "";
        }
    }

    /**
     * 取人员基本信息字典代码含义
     *
     * @param code
     * @return
     */
    public String queryPersonInfo(String id, String code) {
        try {
            return caches.get("app_dic_personinfo:concat(id,code):name").get(id + code);
        } catch (ExecutionException ex) {
            return "";
        }
    }

    /**
     * 取下级地址列
     *
     * @param code
     * @return
     */
    public Map<String, String> queryUnderArea(String code) {
        return fetchUnderData("t_dic_area", "code", "explain_memo", "upper_code", code);
    }

    /**
     * 从字典表中获取下级列表（没有缓存）
     *
     * @param dictTable
     * @param codeColName
     * @param meaningColName
     * @param upperCodeColName
     * @return
     */
    private Map<String, String> fetchUnderData(String dictTable, String codeColName, String meaningColName, String upperCodeColName, String upperCodeName) {
        Map<String, String> map = new HashMap<>();
        List<Map<String, Object>> result = jt.queryForList(
                String.format("select %s as code, %s as meaning from %s where %s = '%s'",
                        codeColName, meaningColName, dictTable, upperCodeColName, upperCodeName));
        for (Map<String, Object> row : result) {
            map.put((String) row.get("code"), (String) row.get("meaning"));
        }
        return map;
    }

}
