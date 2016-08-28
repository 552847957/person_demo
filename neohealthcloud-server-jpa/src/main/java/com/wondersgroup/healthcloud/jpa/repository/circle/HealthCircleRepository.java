package com.wondersgroup.healthcloud.jpa.repository.circle;

import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondersgroup.healthcloud.jpa.entity.circle.HealthCircle;

public interface HealthCircleRepository extends JpaRepository<HealthCircle,String>{

    @Query(value = "select h from HealthCircle h where h.registerid=?1 and h.delFlag='0' and h.sendtime<?2  ")
    Page<HealthCircle> findUserCircle(String registerId,Date flag,Pageable pageable);

    @Query(nativeQuery = true ,value = "select h.* from app_tb_healthcommunity h left join (select count(c.id) as num,c.articleid,c.del_flag from app_tb_communitydiscuss c where c.del_flag=0  GROUP BY  c.articleid ) t1 on h.id=t1.articleid where h.sendtime>=?1 and h.del_flag=0 and h.sendtime<?2 and ifnull(t1.num,0)<=?3 and (h.praisenum*0.2)<=?4  ORDER BY (ifnull(t1.num,0)+h.praisenum*0.2) DESC,sendtime DESC limit 15")
    List<HealthCircle> findHotCircle(String time,String flag,Integer num,Float praisenum);

    @Query(nativeQuery = true ,value = "select h.* from app_tb_healthcommunity h left join (select count(c.id) as num,c.articleid,c.del_flag from app_tb_communitydiscuss c where c.del_flag=0  GROUP BY  c.articleid ) t1 on h.id=t1.articleid where h.sendtime>=?1 and h.del_flag=0 and h.sendtime<?2  ORDER BY (ifnull(t1.num,0)+h.praisenum*0.2) DESC,sendtime DESC limit 15")
    List<HealthCircle> findHotCircle(String time,String flag);

    @Query(value = "select h from HealthCircle h where  h.delFlag='0' and h.sendtime<?1  ")
    Page<HealthCircle> findAllCircle(Date flag,Pageable pageable);


    @Query(value = "select h from HealthCircle h where h.registerid=?1 and h.delFlag='0' and h.id=?2  ")
    HealthCircle findUserAndCircle(String registerId,String articleId);
}
