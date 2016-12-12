package com.wondersgroup.healthcloud.jpa.repository.cloudtopline;

import com.wondersgroup.healthcloud.jpa.entity.bbs.CircleCategory;
import com.wondersgroup.healthcloud.jpa.entity.cloudtopline.CloudTopLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 云头条
 * Created by xianglinhai on 2016/12/9.
 */
public interface CloudTopLineRepository extends JpaRepository<CloudTopLine, Integer> {

}
