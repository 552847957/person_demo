package com.wondersgroup.healthcloud.services.bbs.impl;

import com.wondersgroup.healthcloud.services.bbs.TopicTabService;
import com.wondersgroup.healthcloud.services.bbs.criteria.TopicTabSearchCriteria;
import com.wondersgroup.healthcloud.utils.searchCriteria.JdbcQueryParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * Created by ys on 2016/08/18.
 * @author ys
 */
@Service("topicTabService")
public class TopicTabServiceImpl implements TopicTabService {


    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Map<String, Object>> getTopicTabListByCriteria(TopicTabSearchCriteria searchCriteria) {
        JdbcQueryParams queryParams = searchCriteria.toQueryParams();
        StringBuffer querySql = new StringBuffer("select topic_tab.*,circle.name as circle_name from tb_bbs_topic_tab topic_tab ");
        querySql.append(" left join tb_bbs_circle circle on circle.id=topic_tab.circle_id ");
        List<Object> elelmentType = queryParams.getQueryElementType();
        if (!elelmentType.isEmpty()){
            querySql.append(" where " + queryParams.getQueryString());
        }
        querySql.append(searchCriteria.getOrderInfo());
        querySql.append(searchCriteria.getLimitInfo());
        List<Map<String, Object>> list = jdbcTemplate.queryForList(querySql.toString(), elelmentType.toArray());
        return list;
    }

    @Override
    public int countTopicTabByCriteria(TopicTabSearchCriteria searchCriteria) {
        JdbcQueryParams queryParams = searchCriteria.toQueryParams();
        StringBuffer querySql = new StringBuffer("select count(*) from tb_bbs_topic_tab topic_tab ");
        List<Object> elelmentType = queryParams.getQueryElementType();
        if (!elelmentType.isEmpty()){
            querySql.append(" where " + queryParams.getQueryString());
        }
        Integer rs = jdbcTemplate.queryForObject(querySql.toString(), queryParams.getQueryElementType().toArray(), Integer.class);
        return rs == null ? 0 : rs;
    }

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

}
