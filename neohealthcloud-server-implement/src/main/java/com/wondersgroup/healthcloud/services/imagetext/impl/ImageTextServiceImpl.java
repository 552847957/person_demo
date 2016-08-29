package com.wondersgroup.healthcloud.services.imagetext.impl;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.imagetext.GImageText;
import com.wondersgroup.healthcloud.jpa.entity.imagetext.ImageText;
import com.wondersgroup.healthcloud.jpa.entity.notice.Notice;
import com.wondersgroup.healthcloud.jpa.repository.imagetext.GImageTextRepository;
import com.wondersgroup.healthcloud.jpa.repository.imagetext.ImageTextRepository;
import com.wondersgroup.healthcloud.services.imagetext.ImageTextService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public List<ImageText> findImageTextByAdcode(String mainArea, String specArea, ImageText imageText) {
        try {
            List<ImageText> appAdsList = findAll(imageText);

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

    List<ImageText> findAll(final ImageText imgText) {
        return imageTextRepository.findAll(new Specification<ImageText>() {
            @Override
            public Predicate toPredicate(Root<ImageText> rt, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                List<Predicate> pdList = new ArrayList<Predicate>();
                if (imgText.getDelFlag() != null) {
                    pdList.add(cb.equal(rt.<String>get("delFlag"), imgText.getDelFlag()));
                }
                if (imgText.getMainArea() != null) {
                    pdList.add(cb.equal(rt.<String>get("mainArea"), imgText.getMainArea()));
                }
                if (imgText.getSpecArea() != null) {
                    pdList.add(cb.equal(rt.<String>get("specArea"), imgText.getSpecArea()));
                }
                if (imgText.getAdcode() != null) {
                    pdList.add(cb.equal(rt.<String>get("adcode"), imgText.getAdcode()));
                }
                if (imgText.getVersion() != null) {
                    pdList.add(cb.equal(rt.<String>get("version"), imgText.getVersion()));
                }
                if (imgText.getStartTime() != null) {
                    pdList.add(cb.greaterThanOrEqualTo(rt.<Date>get("startTime"), imgText.getStartTime()));
                }
                if (imgText.getEndTime() != null) {
                    pdList.add(cb.lessThanOrEqualTo(rt.<Date>get("endTime"), imgText.getEndTime()));
                }
                if (pdList.size() > 0) {
                    Predicate[] predicates = new Predicate[pdList.size()];
                    cq.where(pdList.toArray(predicates));
                }
                return null;
            }
        });
    }

    @Override
    public List<String> findGImageTextVersions(String mainArea, String specArea, Integer gadcode) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT version FROM app_tb_neo_g_image_text WHERE  main_area = '").append(mainArea).append("'")
                .append(" AND gadcode = ").append(gadcode);
        if (StringUtils.isNotEmpty(specArea)) {
            sql.append(" AND spec_area = '").append(specArea).append("'");
        }
        return getJt().queryForList(sql.toString(), new Object[]{}, String.class);
    }

    @Override
    public List<GImageText> findGImageTextList(String mainArea, String specArea, Integer gadcode, String version) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM app_tb_neo_g_image_text WHERE  main_area = '").append(mainArea).append("'")
                .append(" AND gadcode = ").append(gadcode);
        if (StringUtils.isNotEmpty(specArea)) {
            sql.append(" AND spec_area = '").append(specArea).append("'");
        }
        if (StringUtils.isNotEmpty(version)) {
            sql.append(" AND version = '").append(version).append("'");
        }
        return getJt().query(sql.toString(), new Object[]{}, new BeanPropertyRowMapper<GImageText>(GImageText.class));
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
        GImageText gImageText = gImageTextRepository.findGImageTextForApp(mainArea, specArea, gadcode, version);
        if (gImageText != null) {
            List<ImageText> imageTextList = imageTextRepository.findByGid(gImageText.getId());
            return imageTextList;
        }
        return null;
    }

    @Override
    @Transactional
    public boolean saveGImageText(GImageText gImageText) {
        try {
            List<ImageText> imageTexts = gImageText.getImages();

            if (gImageText.getId() == null) {
                Date now = new Date();
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
}
