package com.wondersgroup.healthcloud.services.doctor.impl;

import com.wondersgroup.healthcloud.jpa.entity.doctorarticle.DoctorArticle;
import com.wondersgroup.healthcloud.jpa.repository.doctorarticle.DoctorArticleCategoryRepository;
import com.wondersgroup.healthcloud.jpa.repository.doctorarticle.DoctorArticleRepository;
import com.wondersgroup.healthcloud.services.doctor.ManageDoctorArticleService;
import com.wondersgroup.healthcloud.services.doctor.entity.Doctor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by longshasha on 16/9/1.
 */
@Service
public class ManageDoctorArticleServiceImpl implements ManageDoctorArticleService {

    @Autowired
    private DoctorArticleRepository doctorArticleRepository;

    @Autowired
    private JdbcTemplate jt;




    @Override
    public List<DoctorArticle> findAppShowListByCategoryId(int cat_id, int pageSize, int page) {

        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC,"id"));

        List<DoctorArticle> rt = doctorArticleRepository.findListByCategoryId(cat_id, new PageRequest(page - 1, pageSize, sort));
        return rt;


    }

    @Override
    public List<DoctorArticle> findArticleListByIds(List<Integer> ids) {
        return doctorArticleRepository.findArticleListByIds(ids);
    }


    @Override
    public List<Map<String, Object>> findDoctorArticleListByPager(int pageNum, int size, Map<String, Object> parameter) {
        String query = " select a.id,a.title,a.is_visable,a.category_ids,a.update_time from app_tb_doctor_article a ";
        String sql = query + getWhereSqlByParameter(parameter)+" LIMIT " +(pageNum-1)*size +"," + size;
        List<Map<String, Object>> mapList = jt.queryForList(sql);
        return mapList;
    }

    @Override
    public int countDoctorArticleByParameter(Map<String, Object> parameter) {
        String sql = "select count(a.id)  "+
                " from app_tb_doctor_article a " +
                getWhereSqlByParameter(parameter);
        Integer count = jt.queryForObject(sql, Integer.class);
        return count == null ? 0 : count;
    }

    @Override
    public String findCategoryNamesByIds(String categoryIds) {
        if(StringUtils.isBlank(categoryIds)){
            return "";
        }
        String[] ids = categoryIds.split(",");

        StringBuffer sb = new StringBuffer();
        for(String str : ids){
            sb.append(",'"+str+"'");
        }
        String param = sb.toString();
        String sql = " select GROUP_CONCAT(b.c_name) as categoryNames from " +
                " (select a.c_name from app_tb_doctor_article_category a where a.id in (%s)) b";
        sql = String.format(sql,param.substring(1));

        return jt.queryForMap(sql).get("categoryNames").toString();
    }


    public String getWhereSqlByParameter(Map<String, Object> parameter){
        StringBuffer bf = new StringBuffer();
        bf.append(" where 1=1  ");
        if(parameter.size()>0){
            if(parameter.containsKey("title") &&  StringUtils.isNotBlank(parameter.get("title").toString())){
                bf.append(" and a.title like '%"+parameter.get("title").toString()+"%' ");
            }
            if(parameter.containsKey("isVisable") && StringUtils.isNotBlank(parameter.get("isVisable").toString())){
                bf.append(" and a.is_visable = "+parameter.get("isVisable").toString());
            }
            if(parameter.containsKey("startTime") && StringUtils.isNotBlank(parameter.get("startTime").toString())){
                Date startDate = (Date)parameter.get("startTime");
                if(startDate!=null){
                    bf.append(" and a.update_time >= "+startDate);
                }

            }
            if(parameter.containsKey("endTime") && StringUtils.isNotBlank(parameter.get("endTime").toString())){
                Date endDate = (Date)parameter.get("endTime");
                if(endDate!=null){
                    bf.append(" and a.update_time <= "+endDate);
                }
            }
        }
        return bf.toString();
    }
}
