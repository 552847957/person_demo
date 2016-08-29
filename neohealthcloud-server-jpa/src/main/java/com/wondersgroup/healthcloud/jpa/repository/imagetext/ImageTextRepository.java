package com.wondersgroup.healthcloud.jpa.repository.imagetext;

import com.wondersgroup.healthcloud.jpa.entity.imagetext.ImageText;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by zhaozhenxing on 2016/6/12.
 */
public interface ImageTextRepository extends JpaRepository<ImageText, String>, JpaSpecificationExecutor<ImageText> {
    List<ImageText> findByGid(String gid);
}
