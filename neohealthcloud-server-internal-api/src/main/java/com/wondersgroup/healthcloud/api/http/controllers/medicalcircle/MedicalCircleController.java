package com.wondersgroup.healthcloud.api.http.controllers.medicalcircle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wondersgroup.healthcloud.api.utils.Pager;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorAccount;
import com.wondersgroup.healthcloud.jpa.entity.medicalcircle.MedicalCircle;
import com.wondersgroup.healthcloud.jpa.entity.medicalcircle.MedicalCircleTag;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorAccountRepository;
import com.wondersgroup.healthcloud.jpa.repository.medicalcircle.MedicalCircleRepository;
import com.wondersgroup.healthcloud.jpa.repository.medicalcircle.MedicalCircleTagRepository;
import com.wondersgroup.healthcloud.services.medicalcircle.MedicalCircleService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Created by shenbin on 16/8/28.
 */
@RestController
@RequestMapping("api")
public class MedicalCircleController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MedicalCircleController.class);
    @Autowired
    private MedicalCircleRepository medicalCircleRepository;
    @Autowired
    private MedicalCircleService medicalCircleService;

    @Autowired
    private MedicalCircleTagRepository medicalCircleTagRepository;

    @Autowired
    private DoctorAccountRepository doctorAccountRepository;



    //-------------------- 医学圈--帖子/病历-----------------------
    /**
     * 查询帖子病例列表
     * @param
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "medicalCircle/list", method = RequestMethod.POST)
    public Pager findMedicalCircleList(@RequestBody Pager pager){
        int pageNum = 1;
        if(pager.getNumber()!=0)
            pageNum = pager.getNumber();

        List<MedicalCircle> rt = medicalCircleService.findMedicalCircleListByPager(pageNum - 1, pager.getSize());
        int totalSize = medicalCircleRepository.countTag();
        pager.setTotalElements(totalSize);
        pager.setData(rt);
        return pager;
    }


    /**
     * 批量修改冻结/解冻
     * @return
     */
    @RequestMapping(value = "medicalCircleIsVisible/update", method = RequestMethod.POST)
    public JsonResponseEntity updateMedicalCircleIsVisible(@RequestBody String request) {

        JsonKeyReader reader = new JsonKeyReader(request);
        List<String> ids = reader.readObject("ids", true,List.class);
        String isVisible = reader.readString("isVisible", true);
        medicalCircleRepository.updateIsVisible(ids, isVisible);

        return new JsonResponseEntity(0, "修改成功");
    }

    /**
     * 批量添加标签
     * @param request
     * @return
     */
    @RequestMapping(value = "medicalCircle/batchUpdateTag", method = RequestMethod.POST)
    public JsonResponseEntity batchUpdateTag(@RequestBody String request) {

        JsonKeyReader reader = new JsonKeyReader(request);
        List<String> ids = reader.readObject("ids", true,List.class);
        List<String> tags = reader.readObject("tagIds", true,List.class);
        String tagId = tags.get(0);

        medicalCircleRepository.batchUpdateTagByIds(ids, tagId);

        return new JsonResponseEntity(0, "修改成功");
    }


    /**
     * 保存、修改医学圈子
     * @param
     * @return
     */
    @RequestMapping(value = "medicalCircle/saveMedicalCircle", method = RequestMethod.POST)
    public JsonResponseEntity<String> saveMedicalCircle(@RequestBody  MedicalCircle medicalCircle) {
        JsonResponseEntity jsonResponseEntity = new JsonResponseEntity();
        try {
            if(medicalCircle!=null){
                if (StringUtils.isBlank(medicalCircle.getId())) { // 保存
                    jsonResponseEntity.setCode(1001);
                    jsonResponseEntity.setMsg("参数不正确");
                    return jsonResponseEntity;
                }else{
                    MedicalCircle medicalCircleOld = medicalCircleRepository.findOne(medicalCircle.getId());
                    medicalCircleOld.setContent(medicalCircle.getContent());
                    medicalCircleOld.setIsVisible(medicalCircle.getIsVisible());
                    medicalCircleOld.setTitle(medicalCircle.getTitle());
                    medicalCircleOld.setUpdateDate(new Date());

                    String tagId = "";
                    if(StringUtils.isNotBlank(medicalCircle.getTagid())){
                        String[] tagIds = medicalCircle.getTagid().split(",");
                        tagId = tagIds[0];
                    }

                    medicalCircleOld.setTagid(tagId);

                    medicalCircleRepository.save(medicalCircleOld);
                    jsonResponseEntity.setMsg("保存成功");
                    return jsonResponseEntity;
                }

            }

        } catch (Exception e) {
            String errorMsg = "保存/修改医学圈帖子出错";
            logger.error(errorMsg, e);
            jsonResponseEntity.setCode(1001);
            jsonResponseEntity.setMsg(errorMsg);
        }
        return jsonResponseEntity;
    }

    /**
     * 帖子/病历详情
     * @param id
     * @return
     */
    @RequestMapping(value="medicalCircle/getMedicalCircleById",method = RequestMethod.GET)
    public JsonResponseEntity getMedicalCircleById(@RequestParam String id) {
        JsonResponseEntity jsonResponseEntity = new JsonResponseEntity();
        try {
            MedicalCircle medicalCircle = medicalCircleService.getMedicalCircle(id);
            if (medicalCircle != null) {
                DoctorAccount doctorAccount =doctorAccountRepository.findOne(medicalCircle.getDoctorid());
                medicalCircle.setDoctorName(doctorAccount.getName());
                jsonResponseEntity.setData(medicalCircle);
                return jsonResponseEntity;
            } else {
                jsonResponseEntity.setCode(1002);
                jsonResponseEntity.setMsg("未查询到帖子详情");
            }
        } catch (Exception e) {
            String errorMsg = "查询帖子详情出错";
            logger.error(errorMsg, e);
            jsonResponseEntity.setCode(1001);
            jsonResponseEntity.setMsg(errorMsg);
        }
        return jsonResponseEntity;
    }

    //-------------------------医学圈标签---------------------------


    /**
     * 医学圈标签列表
     * @param pager
     * @return
     */
    @RequestMapping(value = "/medicalCircleTag/list", method = RequestMethod.POST)
    public Pager tabList(@RequestBody Pager pager){
        int pageNum = 1;
        if(pager.getNumber()!=0)
            pageNum = pager.getNumber();

        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC,"updateDate"));

        List<MedicalCircleTag> rt = medicalCircleTagRepository.findTagListByPager(new PageRequest((pageNum-1),pager.getSize(),sort));
        int totalSize = medicalCircleTagRepository.countTag();
        pager.setTotalElements(totalSize);
        pager.setData(rt);
        return pager;
    }

    /**
     * 标签字典数据
     * @return
     */
    @RequestMapping(value = "/medicalCircleTag/dic", method = RequestMethod.GET)
    public JsonListResponseEntity<MedicalCircleTag> tabList(){
        JsonListResponseEntity<MedicalCircleTag> response = new JsonListResponseEntity<>();
        List<MedicalCircleTag> rt = medicalCircleTagRepository.findTagList();
        response.setContent(rt, false, null, null);
        return response;
    }

    /**
     * 修改医学圈标签
     * @return
     */
    @RequestMapping(value = "medicalCircleTag/save", method = RequestMethod.POST)
    public JsonResponseEntity updateMedicalCircleTag(@RequestBody MedicalCircleTag medicalCircleTag) {
        if(medicalCircleTag!=null){
            if(StringUtils.isBlank(medicalCircleTag.getId())){
                medicalCircleTag.setId(IdGen.uuid());
                medicalCircleTag.setCreateDate(new Date());
            }
            medicalCircleTag.setUpdateDate(new Date());
            medicalCircleTagRepository.save(medicalCircleTag);
            return new JsonResponseEntity(0, "保存成功");
        }

        return new JsonResponseEntity(1001, "保存失败");
    }


    /**
     * 查询标签详情
     * @param id
     * @return
     */
    @RequestMapping(value = "medicalCircleTag/detail", method = RequestMethod.GET)
    public JsonResponseEntity<MedicalCircleTag> getMedicalCircleTagDetail(@RequestParam String  id) {
        JsonResponseEntity<MedicalCircleTag> response = new JsonResponseEntity<>();
        MedicalCircleTag medicalCircleTag = medicalCircleTagRepository.findOne(id);
        if(medicalCircleTag!=null){
            response.setData(medicalCircleTag);
        }else{
            response.setCode(3010);
            response.setMsg("查询失败");
        }
        return response;
    }


    /**
     * 标签批量删除
     * @param ids
     * @return
     */
    @RequestMapping(value = "medicalCircleTag/batchDelete", method = RequestMethod.POST)
    public JsonResponseEntity batchDelete(@RequestBody List<String> ids) {
        JsonResponseEntity<String> response = new JsonResponseEntity<>();
        if(ids!=null && ids.size()>0){

            medicalCircleTagRepository.deleteByIds(ids);
            response.setMsg("删除成功");
        }else{
            response.setCode(3010);
            response.setMsg("删除失败");
        }
        return response;
    }


}
