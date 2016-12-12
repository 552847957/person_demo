package com.wondersgroup.healthcloud.services.cloudTopLine;

import com.wondersgroup.healthcloud.jpa.entity.cloudtopline.CloudTopLine;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/9.
 */
public interface CloudTopLineService {

    /** 添加/修改 CloudTopLine
     * @param entity
     * @return
     */
    CloudTopLine saveCloudTopLine(CloudTopLine entity);

    /** 修改图标地址
     * @param entity
     * @return
     */
    boolean updateCloudTopLineIconById(CloudTopLine entity);

    /** 删除
     * @param id
     * @return
     */
    boolean delCloudTopLineById(Integer id);




    /**
     * 根据条件查询
     * @param paramMap
     * @return
     */
    List<CloudTopLine> queryCloudTopLineByCondition(Map<String,Object> paramMap);



    /**
     * 根据ID修改 云头条内容
     * @param entity
     * @return
     */
    boolean updateCloudTopLineById(CloudTopLine entity);



}
