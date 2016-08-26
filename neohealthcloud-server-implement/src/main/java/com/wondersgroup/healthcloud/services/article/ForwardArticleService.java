package com.wondersgroup.healthcloud.services.article;

import com.wondersgroup.healthcloud.jpa.entity.article.ForwardArticle;
import com.wondersgroup.healthcloud.jpa.repository.article.ForwardArticleRepository;
import com.wondersgroup.healthcloud.services.article.dto.ForwardArticleAPIEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by dukuanxin on 2016/8/25.
 */
@Component
public class ForwardArticleService {

    @Autowired
    private ForwardArticleRepository repository;

    @Autowired
    private DataSource dataSource;
    private JdbcTemplate jt;


    public int updateForwardArticle(ForwardArticle forwardArticle){
        forwardArticle.setCreate_time(new Date());
       return repository.saveAndFlush(forwardArticle).getId();
    }

    public List<ForwardArticleAPIEntity> queryById(int id){
        String sql="SELECT t1.id,t2.rank,t1.title,t2.article_id,t2.start_time,t2.end_time FROM app_tb_neoarticle t1 " +
                "LEFT JOIN app_tb_neoforward_article t2 ON t1.id=t2.article_id where id="+id;
        List<Map<String, Object>> maps = getJt().queryForList(sql);

        return mapTOforwardArticle(maps);

    }

    public List<ForwardArticleAPIEntity> queryPageForWardArticle(int status,int pageNo,int pageSize){
        String sql=makeSql(status,1);
        sql+=" order by t2.rank desc limit "+(pageNo-1)*pageSize+","+pageSize;
        List<Map<String, Object>> maps = getJt().queryForList(sql);
        return mapTOforwardArticle(maps);
    }
    public int getCount(int status){
        String sql = makeSql(status,2);
        return this.getJt().queryForObject(sql,Integer.class);
    }
    public String makeSql(int status,int type){
        StringBuffer sql = new StringBuffer();
        if(type==1){
            sql.append("SELECT t1.id,t2.rank,t1.title,t2.article_id,t2.start_time,t2.end_time FROM app_tb_neoarticle t1 " +
                    "LEFT JOIN app_tb_neoforward_article t2 ON t1.id=t2.article_id where 1=1");
        }else{
            sql.append("SELECT count(1) FROM app_tb_neoarticle t1 LEFT JOIN app_tb_neoforward_article t2 ON t1.id=t2.article_id where 1=1");
        }
        if(status==1){//未开始
            sql.append(" and start_time<NOW()");
        }
        if(status==2){//进行中
            sql.append(" and start_time>=OW() and end_time<=now()");
        }
        if(status==3){//已结束
            sql.append(" and end_tiem_time>NOW()");
        }
        return sql.toString();
    }

    public List<ForwardArticleAPIEntity> mapTOforwardArticle(List<Map<String,Object>> param){
        List<ForwardArticleAPIEntity> list=new ArrayList<>();
        for(Map<String,Object> map:param){
            list.add(new ForwardArticleAPIEntity(map));
        }
        return  list;
    }
    private JdbcTemplate getJt() {
        if (jt == null) {
            jt = new JdbcTemplate(dataSource);
        }
        return jt;
    }
}