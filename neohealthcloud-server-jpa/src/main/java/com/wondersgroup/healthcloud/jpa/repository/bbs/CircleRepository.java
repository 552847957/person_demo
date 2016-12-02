package com.wondersgroup.healthcloud.jpa.repository.bbs;

import com.wondersgroup.healthcloud.jpa.entity.bbs.Circle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
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

    List<Circle> queryByCateIdAndDelflagOrderByRankDesc(Integer cateId, String delflag);

    // 默认加入的圈子列表
    List<Circle> queryByIsDefaultAttentAndDelflag(Integer isDefaultAttent, String delflag);

    // 查询推荐圈子列表
    List<Circle> queryByIsRecommendAndDelflagOrderByRankDesc(Integer isRecommend, String delflag);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "update tb_bbs_circle set attention_count = (select count(uid) from tb_bbs_user_circle where del_flag = '0' and circle_id = ?1) where id = ?1")
    int updateActuallyAttentionCount(Integer circleId);
}
