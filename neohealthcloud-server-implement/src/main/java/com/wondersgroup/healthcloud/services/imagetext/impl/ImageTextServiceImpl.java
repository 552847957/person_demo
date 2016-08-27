package com.wondersgroup.healthcloud.services.imagetext.impl;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.imagetext.ImageText;
import com.wondersgroup.healthcloud.jpa.repository.imagetext.ImageTextRepository;
import com.wondersgroup.healthcloud.services.imagetext.ImageTextService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
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
}
