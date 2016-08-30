package com.wondersgroup.healthcloud.api.http.controllers.doctorarticle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.wondersgroup.healthcloud.api.utils.PropertyFilterUtil;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.doctorarticle.DoctorArticle;
import com.wondersgroup.healthcloud.jpa.entity.doctorarticle.DoctorArticleCategory;
import com.wondersgroup.healthcloud.jpa.repository.doctorarticle.DoctorArticleCategoryRepository;
import com.wondersgroup.healthcloud.jpa.repository.doctorarticle.DoctorArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shenbin on 16/8/30.
 */
@RestController
@RequestMapping(value="api")
public class DoctorArticleController {

    @Autowired
    private DoctorArticleRepository doctorArticleRepository;

    @Autowired
    private DoctorArticleCategoryRepository doctorArticleCategoryRepository;

    /**
     * 查询学苑分类
     * @return
     */
    @VersionRange
    @RequestMapping(value = "doctorArticleCategory/find", method = RequestMethod.GET)
    public JsonResponseEntity findDoctorArticleCategory(){
        List<DoctorArticleCategory> doctorArticleCategories = doctorArticleCategoryRepository.findAll();

        return new JsonResponseEntity(0, "查询成功", doctorArticleCategories);
    }

    /**
     * 查询学苑分类文章
     * @param categoryId
     * @param pageable
     * @return
     * @throws JsonProcessingException
     */
    @VersionRange
    @RequestMapping(value = "doctorArticle/find", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String findDoctorArticle(@RequestParam int categoryId,
                                    @PageableDefault Pageable pageable) throws JsonProcessingException {
        Page<DoctorArticle> doctorArticles = doctorArticleRepository.findByCategoryId(categoryId, pageable);

        Map<Class, Object> filterMap = new HashMap<>();
        filterMap.put(PageImpl.class, new String[]{"content", "total_pages", "total_elements", "size", "number", "last"});
        SimpleFilterProvider filterProvider = PropertyFilterUtil.filterOutAllExceptFilter(filterMap);
        JsonResponseEntity response = new JsonResponseEntity(0, "查询成功", doctorArticles);

        return PropertyFilterUtil.getObjectMapper().setFilterProvider(filterProvider).writeValueAsString(response);
    }


//    @Resource
//    private ManageDoctorArticleService manageDoctorArticleService;
//
//    @Resource
//    private ManageDoctorArticleCategotyService manageDoctorArticleCategoryService;
//
//    @Autowired
//    private AppUrlH5Utils appUrlH5Utils;
//
//    /**
//     * 学苑列表
//     * @param uid
//     * @return
//     */
//    @VersionRange
//    @RequestMapping(value="/articleCat", method = RequestMethod.GET)
//    public JsonResponseEntity<List<DoctorCateArticleListAPIEntity>> articleCat(@RequestParam(required = false) String uid){
//        List<DoctorCateArticleListAPIEntity> cateArticleListAPIEntities = this.getCatArticleEntityList(uid);
//        JsonResponseEntity<List<DoctorCateArticleListAPIEntity>> rt = new JsonResponseEntity<>();
//        rt.setData(cateArticleListAPIEntities);
//        return rt;
//    }
//
//    /**
//     * 学苑文章列表
//     * @param uid
//     * @return
//     */
//    @VersionRange
//    @RequestMapping(value="/articleList", method = RequestMethod.GET)
//    public JsonListResponseEntity<DoctorArticleListAPIEntity> articleList(@RequestParam(required = true) Integer cat_id,
//                                                                          @RequestParam(required = false) String uid,
//                                                                          @RequestParam(required = false, defaultValue = "1") String flag,
//                                                                          @RequestParam(required = false) String order){
//        if (null == flag || "".equals(flag)){
//            flag = "1";
//        }
//        int page = Integer.valueOf(flag);
//        int pageSize = 10;
//
//        List<DoctorArticleListAPIEntity> cateArticleListAPIEntities = this.getArticleEntityList(cat_id, page, pageSize+1);
//        JsonListResponseEntity<DoctorArticleListAPIEntity> rt = new JsonListResponseEntity<>();
//        Boolean hasMore = false;
//        if (null != cateArticleListAPIEntities && !cateArticleListAPIEntities.isEmpty() && cateArticleListAPIEntities.size() > pageSize){
//            cateArticleListAPIEntities = cateArticleListAPIEntities.subList(0, pageSize);
//            hasMore = true;
//        }
//        if (hasMore){
//            flag = String.valueOf(page+1);
//        }
//        rt.setContent(cateArticleListAPIEntities, hasMore, null, flag);
//        return rt;
//    }
//
//    /**
//     * 获取医生下面的分类文章
//     * @param uid 医生的id
//     * @return List<DoctorCateArticleListAPIEntity>
//     */
//    private List<DoctorCateArticleListAPIEntity> getCatArticleEntityList(String uid){
//        List<DoctorCateArticleListAPIEntity> rtList = new ArrayList<>();
//        //获取分类
//        Map<String, Object> catParm = new HashMap<>();
//        catParm.put("is_visable", 1);
//        List<DoctorArticleCategory> cateArticleList = this.manageDoctorArticleCategoryService.findDoctorCategoryByKeys(catParm);
//        if (null == cateArticleList || cateArticleList.isEmpty()){
//            return null;
//        }
//        //最多显示4个分类
//        int showCatNum = 4;
//        if (!cateArticleList.isEmpty() && cateArticleList.size()>showCatNum){
//            cateArticleList = cateArticleList.subList(0, showCatNum);
//        }
//
//        if (!cateArticleList.isEmpty() && cateArticleList.size() > 0) {
//            DoctorCateArticleListAPIEntity cateEntity;
//
//            //遍历文章分类,获取分类下面的文章
//            for (DoctorArticleCategory articleCategory : cateArticleList) {
//                cateEntity = new DoctorCateArticleListAPIEntity();
//                cateEntity.setCat_id(String.valueOf(articleCategory.getId()));
//                cateEntity.setCat_name(articleCategory.getC_name());
//                //获取文章分类下面的文章
//                List<DoctorArticleListAPIEntity> articleList = this.getArticleEntityList(articleCategory.getId(), 1, 11);
//                Boolean hasMore = false;
//                if (null != articleList &&  !articleList.isEmpty() && articleList.size() > 10){
//                    articleList = articleList.subList(0, 10);
//                    hasMore = true;
//                }
//                cateEntity.setMore(hasMore);
//                if (hasMore){
//                    String flag = String.valueOf(2);
//                    cateEntity.setMore_params(null, flag);
//                }
//                cateEntity.setList(articleList);
//                rtList.add(cateEntity);
//            }
//        }
//        if (cateArticleList.size() > 4){
//            rtList = rtList.subList(0, 4);
//        }
//        return rtList;
//    }
//
//    /**
//     * 获取分类下面的文章
//     * @param cat_id 学苑文章的分类id
//     * @return List
//     */
//    private List<DoctorArticleListAPIEntity> getArticleEntityList(int cat_id, int page, int pageSize){
//        //获取文章分类下面的文章
//        List<DoctorArticle> catArticleList = this.manageDoctorArticleService.findAppShowListByCategoryId(cat_id, pageSize, page );
//
//        if(null == catArticleList || catArticleList.size() == 0){
//            return null;
//        }
//        List<DoctorArticleListAPIEntity> articleList = new ArrayList<>();
//        for (DoctorArticle articleModel : catArticleList){
//            DoctorArticleListAPIEntity articleEntity = new DoctorArticleListAPIEntity();
//            articleEntity.setId(String.valueOf(articleModel.getId()));
//            articleEntity.setTitle(articleModel.getTitle());
//            articleEntity.setDesc(articleModel.getBrief());
//            int pv = articleModel.getPv() + articleModel.getFake_pv();
//            articleEntity.setPv(String.valueOf(pv));
//            articleEntity.setThumb(articleModel.getThumb());
//            articleEntity.setUrl(appUrlH5Utils.buildXueYuanArticleView(articleModel.getId()));
//            articleList.add(articleEntity);
//        }
//        return articleList;
//    }
}
