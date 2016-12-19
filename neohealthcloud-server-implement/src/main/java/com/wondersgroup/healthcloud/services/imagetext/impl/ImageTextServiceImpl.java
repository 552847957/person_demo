package com.wondersgroup.healthcloud.services.imagetext.impl;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.wondersgroup.healthcloud.common.appenum.ImageTextEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.imagetext.GImageText;
import com.wondersgroup.healthcloud.jpa.entity.imagetext.ImageText;
import com.wondersgroup.healthcloud.jpa.repository.imagetext.GImageTextRepository;
import com.wondersgroup.healthcloud.jpa.repository.imagetext.ImageTextRepository;
import com.wondersgroup.healthcloud.services.imagetext.ImageTextService;

/**
 * Created by zhaozhenxing on 2016/6/12.
 */
@Service("imageTextService")
public class ImageTextServiceImpl implements ImageTextService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ImageTextServiceImpl.class);

    @Autowired
    private ImageTextRepository imageTextRepository;

    @Autowired
    private GImageTextRepository gImageTextRepository;

    @Autowired
    private DataSource dataSource;
    private JdbcTemplate jt;

    @Override
    public ImageText findImageTextById(String id) {
        return imageTextRepository.findOne(id);
    }

    @Override
    public List<ImageText> findImageTextByAdcodeForApp(String mainArea, String specArea, ImageText imageText) {
        try {
            imageText.setDelFlag(0);
            imageText.setMainArea(mainArea);
            imageText.setSpecArea(specArea);

            StringBuffer strBuf = new StringBuffer();
            strBuf.append("SELECT * FROM app_tb_neoimage_text WHERE 1 = 1");
            strBuf.append(findAll(imageText));
            if (ImageTextEnum.NAVIGATION_BAR.getType().equals(imageText.getAdcode())) {
                strBuf.append(" ORDER BY sequence");
            } else {
                strBuf.append(" ORDER BY sequence DESC");
            }

            List<ImageText> appAdsList = getJt().query(strBuf.toString(), new Object[]{}, new BeanPropertyRowMapper<ImageText>(ImageText.class));

            if (appAdsList != null && appAdsList.size() > 0) {
                return appAdsList;
            }
        } catch (Exception ex) {
            logger.error("ImageTextServiceImpl.findImageTextByAdcodeForApp\t-->\t" + ex.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public int countImageTextByAdcode(Map params) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(1) FROM app_tb_neoimage_text WHERE 1 = 1 ")
                .append(getWhereSqlByParameter(params));
        logger.info("countImageTextByAdcode --> " + sql);
        Integer count = getJt().queryForObject(sql.toString(), Integer.class);
        return count == null ? 0 : count;
    }

    @Override
    public List<ImageText> findImageTextByAdcode(Integer pageNum, Integer pageSize, Map params) {
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT * FROM app_tb_neoimage_text WHERE 1 = 1 ")
                    .append(getWhereSqlByParameter(params))
                    .append(" ORDER BY update_time DESC")
                    .append(" LIMIT " + (pageNum - 1) * pageSize + "," + pageSize);
            logger.info("findImageTextByAdcode --> " + sql);
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
            imageText.setCreateTime(new Date());
            imageText.setUpdate_time(new Date());
        }
        return imageTextRepository.saveAndFlush(imageText);
    }

    @Override
    @Transactional
    public int saveBatchImageText(List<ImageText> imageTextList) {
        int flag = 0;
        for (ImageText imageText : imageTextList) {
            if (StringUtils.isBlank(imageText.getId())) {
                imageText.setId(IdGen.uuid());
            }
            imageTextRepository.saveAndFlush(imageText);
            flag++;
        }
        return flag;
    }

    String findAll(final ImageText imgText) {
        StringBuffer strBuf = new StringBuffer();
        if (imgText.getDelFlag() != null) {
            strBuf.append(" AND del_flag = " + imgText.getDelFlag());
        }
        if (StringUtils.isNotEmpty(imgText.getMainArea())) {
            strBuf.append(" AND main_area = '" + imgText.getMainArea() + "'");
        }
        if (StringUtils.isNotEmpty(imgText.getSpecArea())) {
            strBuf.append(" AND spec_area = '" + imgText.getSpecArea() + "'");
        }
        if (imgText.getAdcode() != null) {
            strBuf.append(" AND adcode = " + imgText.getAdcode());
        }
        if (StringUtils.isNotEmpty(imgText.getVersion())) {
            strBuf.append(" AND version = '" + imgText.getVersion() + "'");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        strBuf.append(" AND ('" + sdf.format(new Date()) + "' BETWEEN start_time AND end_time  OR (start_time IS NULL AND end_time IS NULL))");

        if (StringUtils.isBlank(imgText.getSource())) {
            strBuf.append(" AND source = '1'");
        } else {
            strBuf.append(" AND source = '" + imgText.getSource() + "'");
        }

        return strBuf.toString();
    }

    String findAllForApp(final ImageText imgText) {
        StringBuffer strBuf = new StringBuffer();
        if (imgText.getDelFlag() != null) {
            strBuf.append(" AND del_flag = " + imgText.getDelFlag());
        }
        if (StringUtils.isNotEmpty(imgText.getMainArea())) {
            strBuf.append(" AND main_area = '" + imgText.getMainArea() + "'");
        }
        if (StringUtils.isNotEmpty(imgText.getSpecArea())) {
            strBuf.append(" AND spec_area = '" + imgText.getSpecArea() + "'");
        }
        if (imgText.getAdcode() != null) {
            strBuf.append(" AND adcode = " + imgText.getAdcode());
        }
        if (StringUtils.isNotEmpty(imgText.getVersion())) {
            strBuf.append(" AND version = '" + imgText.getVersion() + "'");
        }
        if (imgText.getStartTime() != null && imgText.getEndTime() != null) {
            strBuf.append(" AND ('" + imgText.getStartTime() + "' BETWEEN start_time AND end_time")
                    .append(" OR '" + imgText.getEndTime() + "' BETWEEN start_time AND end_time")
                    .append(")");
        }

        if (StringUtils.isBlank(imgText.getSource())) {
            strBuf.append(" AND source = '1'");
        } else {
            strBuf.append(" AND source = '" + imgText.getSource() + "'");
        }

        return strBuf.toString();
    }

    @Override
    public List<String> findGImageTextVersions(String mainArea, String specArea, Integer gadcode, String source) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT version FROM app_tb_neo_g_image_text WHERE  main_area = '").append(mainArea).append("'")
                .append(" AND source = '").append(source).append("'")
                .append(" AND gadcode = ").append(gadcode);
        if (StringUtils.isNotEmpty(specArea)) {
            sql.append(" AND spec_area = '").append(specArea).append("'");
        }
        return getJt().queryForList(sql.toString(), new Object[]{}, String.class);
    }

    @Override
    public List<GImageText> findGImageTextList(Integer pageNum, Integer pageSize, Map params) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM app_tb_neo_g_image_text WHERE   1 = 1 ")
                .append(getGWhereSqlByParameter(params))
                .append(" ORDER BY update_time DESC");
                //.append(" LIMIT " + (pageNum - 1) * pageSize + "," + pageSize);
        return getJt().query(sql.toString(), new Object[]{}, new BeanPropertyRowMapper<GImageText>(GImageText.class));
    }

    @Override
    public int countGImageTextList(Map params) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(1) FROM app_tb_neo_g_image_text WHERE   1 = 1 ")
                .append(getGWhereSqlByParameter(params));
        Integer count = getJt().queryForObject(sql.toString(), Integer.class);
        return count == null ? 0 : count;
    }

    @Override
    public GImageText findGImageTextById(String gid) {
        GImageText gImageText = gImageTextRepository.findOne(gid);
        if (gImageText != null) {
            List<ImageText> imageTextList = imageTextRepository.findByGid(gid);
            gImageText.setImages(imageTextList);
        }
        return gImageText;
    }

    @Override
    public List<ImageText> findGImageTextForApp(String mainArea, String specArea, Integer gadcode, String version) {
        if (gadcode != ImageTextEnum.G_HOME_SPECIAL_SERVICE.getType()) {// 仅在查询区级特色服务是需要使用spec_area
            specArea = null;
        }
        GImageText param = new GImageText();
        param.setDelFlag("0");
        param.setMainArea(mainArea);
        param.setSpecArea(specArea);
        param.setGadcode(gadcode);
        param.setVersion(version);
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("SELECT * FROM app_tb_neo_g_image_text WHERE 1 = 1 ");
        strBuf.append(findGImageTextForApp(param));

        GImageText gImageText = null;
        try {
            gImageText = getJt().queryForObject(strBuf.toString(), new BeanPropertyRowMapper<GImageText>(GImageText.class));
        } catch (EmptyResultDataAccessException ex) {
            logger.info("ImageTextServiceImpl.findGImageTextForApp --> " + ex.getLocalizedMessage());
            return null;
        } catch (IncorrectResultSizeDataAccessException ex) {
            logger.info("ImageTextServiceImpl.findGImageTextForApp --> " + ex.getLocalizedMessage());
            return null;
        }

        if (gImageText != null) {
            List<ImageText> imageTextList = imageTextRepository.findByGidForApp(gImageText.getId());
            return imageTextList;
        }
        return null;
    }

    @Override
    @Transactional
    public boolean saveGImageText(GImageText gImageText) {
        try {
            List<ImageText> imageTexts = gImageText.getImages();

            Date now = new Date();
            if (gImageText.getId() == null) {
                String gid = IdGen.uuid();
                gImageText.setId(gid);
                gImageText.setCreateTime(now);
                gImageText.setUpdateTime(now);
                for (int i = 0; i < imageTexts.size(); i++) {
                    imageTexts.get(i).setId(IdGen.uuid());
                    imageTexts.get(i).setGid(gid);
                    imageTexts.get(i).setCreateTime(now);
                    imageTexts.get(i).setUpdate_time(now);
                    imageTexts.get(i).setDelFlag(0);
                    imageTexts.get(i).setSource(gImageText.getSource());
                }
            } else {
                gImageText.setUpdateTime(now);
                for (int i = 0; i < imageTexts.size(); i++) {
                    if (imageTexts.get(i).getId() == null) {
                        imageTexts.get(i).setId(IdGen.uuid());
                        imageTexts.get(i).setGid(gImageText.getId());
                        imageTexts.get(i).setCreateTime(now);
                        imageTexts.get(i).setDelFlag(0);
                        imageTexts.get(i).setSource(gImageText.getSource());
                    }
                    imageTexts.get(i).setUpdate_time(now);
                }
            }
            gImageTextRepository.save(gImageText);
            imageTextRepository.save(imageTexts);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    private JdbcTemplate getJt() {
        if (jt == null) {
            jt = new JdbcTemplate(dataSource);
        }
        return jt;
    }

    // 生成组图SQL
    private String getGWhereSqlByParameter(Map parameter) {
        StringBuffer bf = new StringBuffer();
        if (parameter.size() > 0) {
            Object tmpObj = parameter.get("main_area");
            if (tmpObj != null && StringUtils.isNotBlank(tmpObj.toString())) {
                bf.append(" and main_area = '" + tmpObj + "'");
            }
            tmpObj = parameter.get("spec_area");
            if (tmpObj != null && StringUtils.isNotBlank(tmpObj.toString())) {
                bf.append(" and spec_area = '" + tmpObj + "'");
            } else {
                bf.append(" and spec_area is null");
            }
            tmpObj = parameter.get("gadcode");
            if (tmpObj != null && StringUtils.isNotBlank(tmpObj.toString())) {
                bf.append(" and gadcode = " + tmpObj);
            }
            tmpObj = parameter.get("version");
            if (tmpObj != null && StringUtils.isNotBlank(tmpObj.toString())) {
                bf.append(" and version = '" + tmpObj + "'");
            }
            tmpObj = parameter.get("source");
            if (tmpObj != null && StringUtils.isNotBlank(tmpObj.toString())) {
                bf.append(" and source = '" + tmpObj + "'");
            }
        }
        return bf.toString();
    }

    // 生成图文SQL
    private String getWhereSqlByParameter(Map parameter) {
        StringBuffer bf = new StringBuffer();
        if (parameter.size() > 0) {
            Object tmpObj = parameter.get("mainArea");
            if (tmpObj != null && StringUtils.isNotBlank(tmpObj.toString())) {
                bf.append(" and main_area = '" + tmpObj + "' ");
            }
            tmpObj = parameter.get("specArea");
            if (tmpObj != null && StringUtils.isNotBlank(tmpObj.toString())) {
                bf.append(" and (spec_area is null or spec_area = '" + tmpObj + "')");
            }
            tmpObj = parameter.get("adcode");
            if (tmpObj != null && StringUtils.isNotBlank(tmpObj.toString())) {
                bf.append(" and adcode = " + tmpObj);
            }
            tmpObj = parameter.get("version");
            if (tmpObj != null && StringUtils.isNotBlank(tmpObj.toString())) {
                bf.append(" and version = '" + tmpObj + "'");
            }
            tmpObj = parameter.get("delFlag");
            if (tmpObj != null && StringUtils.isNotBlank(tmpObj.toString())) {
                bf.append(" and del_flag = '" + tmpObj + "'");
            }
            tmpObj = parameter.get("source");
            if (tmpObj != null && StringUtils.isNotBlank(tmpObj.toString())) {
                bf.append(" and source = '" + tmpObj + "'");
            }
            tmpObj = parameter.get("startTime");
            Object tmpObja = parameter.get("endTime");
            if (tmpObj != null && StringUtils.isNotBlank(tmpObj.toString()) && tmpObja != null && StringUtils.isNotBlank(tmpObja.toString())) {
                bf.append(" AND ('" + tmpObj + "' BETWEEN start_time AND end_time")
                        .append(" OR '" + tmpObja + "' BETWEEN start_time AND end_time")
                        .append(")");
            }
        }
        return bf.toString();
    }

    private String findGImageTextForApp(GImageText gImageText) {
        StringBuffer strBuf = new StringBuffer();

        if (StringUtils.isNotEmpty(gImageText.getDelFlag())) {
            strBuf.append(" AND del_flag = '" + gImageText.getDelFlag() + "'");
        }
        if (StringUtils.isNotEmpty(gImageText.getMainArea())) {
            strBuf.append(" AND main_area = '" + gImageText.getMainArea() + "'");
        }
        if (StringUtils.isNotEmpty(gImageText.getSpecArea())) {
            strBuf.append(" AND spec_area = '" + gImageText.getSpecArea() + "'");
        } else {
            strBuf.append(" AND spec_area IS NULL");
        }
        if (gImageText.getGadcode() != null) {
            strBuf.append(" AND gadcode = " + gImageText.getGadcode().intValue());
        }
        if (StringUtils.isNotEmpty(gImageText.getVersion())) {
            strBuf.append(" AND version = '" + gImageText.getVersion() + "'");
        }

        return strBuf.toString();
    }

}
