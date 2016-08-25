package com.wondersgroup.healthcloud.services.imagetext.impl;

//import com.vaccine.api.implement.dto.advertisement.HomeDiscoveryDTO;
//import com.vaccine.api.implement.dto.advertisement.LoadingImageDTO;

import com.wondersgroup.healthcloud.common.appenum.ImageTextEnum;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.imagetext.ImageText;
import com.wondersgroup.healthcloud.jpa.repository.imagetext.ImageTextRepository;
import com.wondersgroup.healthcloud.services.imagetext.ImageTextService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by zhaozhenxing on 2016/6/12.
 */
@Service("imageTextService")
public class ImageTextServiceImpl implements ImageTextService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ImageTextServiceImpl.class);

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jt;

    @Autowired
    private ImageTextRepository imageTextRepository;

    @Override
    public List<ImageText> findImageTextByAdcode(String mainArea, String specArea, ImageTextEnum adcode) {
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT * FROM app_tb_neoimage_text WHERE del_flag = '0'");

            if (!StringUtils.isEmpty(mainArea)) {
                sql.append(" AND main_area = '").append(mainArea).append("'");
            }
            if (!StringUtils.isEmpty(specArea)) {
                sql.append(" AND spec_area = '").append(specArea).append("'");
            }
            if (adcode != null) {
                sql.append(" AND adcode = ").append(adcode.getType());
            }
            List<ImageText> appAdsList = getJt().query(sql.toString(), new Object[]{}, new BeanPropertyRowMapper<ImageText>(ImageText.class));

            if (appAdsList != null && appAdsList.size() > 0) {
                return appAdsList;
            }
        } catch (Exception ex) {
            logger.error("ImageTextServiceImpl.findImageTextByAdcode\t-->\t" + ex.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public ImageText saveImageText(ImageText imageText) {
        if (StringUtils.isBlank(imageText.getId())) {
            imageText.setId(IdGen.uuid());
        }
        return imageTextRepository.saveAndFlush(imageText);
    }

    /**
     * 获取jdbc template
     *
     * @return
     */
    private JdbcTemplate getJt() {
        if (jt == null) {
            jt = new JdbcTemplate(dataSource);
        }
        return jt;
    }
}
