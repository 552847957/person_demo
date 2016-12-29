package com.wondersgroup.healthcloud.services.diabetes;

import com.wondersgroup.healthcloud.jpa.entity.diabetes.TubeRelation;

/**
 * Created by zhuchunliu on 2016/12/14.
 */
public interface TubeRelationService {
    /**
     * 获取用户的在管信息
     * @param registerid 用户主键
     * @param fresh 动态web端校验在管关系，默认true
     * @return
     */
    public TubeRelation getTubeRelation(String registerid,Boolean fresh);

    /**
     * 根据身份证号获取用户的在管信息
     * @param personCard
     * @return
     */
    public TubeRelation getTubeRelationByPersonCard(String personCard);
}
