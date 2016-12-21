package com.wondersgroup.healthcloud.jpa.repository.bbs;

import com.wondersgroup.healthcloud.jpa.entity.bbs.Circle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Iterator;
import java.util.List;


/**
 * Created by ys on 2016/08/11
 * 圈子
 */
public interface CircleRepository extends JpaRepository<Circle, Integer> {

    @Query(nativeQuery = true,
            value = "select c.* from tb_bbs_user_circle u left join tb_bbs_circle c on u.circle_id=c.id" +
                    " where u.uid=?1 and u.del_flag = 0 ORDER BY u.update_time DESC")
    List<Circle> findUserJoinedCircles(String uid);

    /**
     * 包含取消关注的
     */
    @Query(nativeQuery = true,
            value = "select u.circle_id from tb_bbs_user_circle u where u.uid=?1")
    List<Integer> getAllUserJoinedCircleId(String uid);

    // 默认加入的圈子列表
    @Query("select c from Circle c where c.delFlag = '0' and c.isDefaultAttent=1 ORDER BY c.rank DESC, c.updateTime DESC")
    List<Circle> findAllDefaultAttents();

    @Query("select c from Circle c where c.delFlag = '0' ORDER BY c.rank DESC, c.updateTime DESC")
    List<Circle> findAllVaild();

    @Query("select c from Circle c ORDER BY c.rank DESC, c.updateTime DESC")
    List<Circle> findAll();

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "update tb_bbs_circle set attention_count = (select count(uid) from tb_bbs_user_circle where del_flag = '0' and circle_id = ?1) where id = ?1")
    int updateActuallyAttentionCount(Integer circleId);
    
    Circle queryByName(String name);
    
    //查询猜你喜欢的圈子s(不能包含默认关注的)
    @Query(value="SELECT t.* FROM tb_bbs_circle t WHERE t.is_recommend=1 AND t.is_default_attent=0 AND t.del_flag = 0 " +
            " AND t.id NOT in(" +
            " SELECT bc.id FROM tb_bbs_circle bc LEFT JOIN tb_bbs_user_circle buc ON bc.id=buc.circle_id " +
            " WHERE bc.is_recommend=1 AND buc.del_flag=0 AND buc.uid= ?1 GROUP BY bc.id" +
            ")",nativeQuery=true)
    List<Circle> findGuessLikeCircles(String uid);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "update tb_bbs_circle t set t.topic_count=t.topic_count+1 where t.id=?1")
    void incTopicCount(Integer circleId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "update tb_bbs_circle t set t.topic_count=t.topic_count+?2 where t.id=?1")
    void incTopicCount(Integer circleId, int addCount);

}
