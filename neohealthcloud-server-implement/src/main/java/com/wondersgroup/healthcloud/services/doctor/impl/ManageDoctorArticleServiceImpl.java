package com.wondersgroup.healthcloud.services.doctor.impl;

import com.wondersgroup.healthcloud.jpa.entity.doctorarticle.DoctorArticle;
import com.wondersgroup.healthcloud.jpa.repository.doctorarticle.DoctorArticleRepository;
import com.wondersgroup.healthcloud.services.doctor.ManageDoctorArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by longshasha on 16/9/1.
 */
@Service
public class ManageDoctorArticleServiceImpl implements ManageDoctorArticleService {

    @Autowired
    private DoctorArticleRepository doctorArticleRepository;



    @Override
    public List<DoctorArticle> findAppShowListByCategoryId(int cat_id, int pageSize, int page) {

        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC,"id"));

        List<DoctorArticle> rt = doctorArticleRepository.findListByCategoryId(cat_id, new PageRequest(page - 1, pageSize, sort));
        return rt;


    }
}
