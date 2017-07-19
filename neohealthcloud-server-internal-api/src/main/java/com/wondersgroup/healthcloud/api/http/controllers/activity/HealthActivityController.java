package com.wondersgroup.healthcloud.api.http.controllers.activity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import com.wondersgroup.healthcloud.api.http.dto.activity.UserInfoDTO;
import com.wondersgroup.healthcloud.jpa.entity.activity.HealthActivityDetail;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.wondersgroup.healthcloud.api.http.dto.activity.HealthActivityInfoDTO;
import com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle.CaseAPIEntity;
import com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle.CommentAPIEntity;
import com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle.DynamicAPIEntity;
import com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle.ImageAPIEntity;
import com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle.MedicalCircleDependence;
import com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle.MedicalCircleDetailAPIEntity;
import com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle.NoteAPIEntity;
import com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle.ShareAPIEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.dict.DictCache;
import com.wondersgroup.healthcloud.jpa.entity.activity.HealthActivityInfo;
import com.wondersgroup.healthcloud.jpa.entity.area.DicArea;
import com.wondersgroup.healthcloud.jpa.entity.circle.ArticleAttach;
import com.wondersgroup.healthcloud.jpa.entity.circle.ArticleTransmit;
import com.wondersgroup.healthcloud.jpa.entity.medicalcircle.MedicalCircle;
import com.wondersgroup.healthcloud.jpa.entity.medicalcircle.MedicalCircleCommunity;
import com.wondersgroup.healthcloud.jpa.entity.medicalcircle.MedicalCircleReply;
import com.wondersgroup.healthcloud.jpa.repository.activity.HealthActivityDetailRepository;
import com.wondersgroup.healthcloud.jpa.repository.activity.HealthActivityInfoRepository;
import com.wondersgroup.healthcloud.jpa.repository.area.DicAreaRepository;
import com.wondersgroup.healthcloud.services.doctor.DoctorService;
import com.wondersgroup.healthcloud.services.doctor.entity.Doctor;
import com.wondersgroup.healthcloud.services.medicalcircle.MedicalCircleService;
import com.wondersgroup.healthcloud.services.user.HealthActivityInfoService;
import com.wondersgroup.healthcloud.utils.DateFormatter;
import com.wondersgroup.healthcloud.utils.ImageUtils;
import com.wondersgroup.healthcloud.utils.TimeAgoUtils;
import com.wondersgroup.healthcloud.utils.circle.CircleLikeUtils;

@RestController
@RequestMapping("/healthActivity")
public class HealthActivityController {

    private Logger                    logger = Logger.getLogger(HealthActivityController.class);

    @Autowired
    private HealthActivityInfoService infoService;
    @Autowired
    private HealthActivityInfoRepository      activityRepo;
    @Autowired
    private DicAreaRepository dicAreaRepository;
    @Autowired
    private HttpRequestExecutorManager httpRequestExecutorManager;
    @Autowired
    private MedicalCircleService    mcService;
    @Autowired
    private DictCache               dictCache;
    @Autowired
    private DoctorService           docinfoService;
    @Autowired
    private CircleLikeUtils circleLikeUtils;
    @Autowired
    private MedicalCircleService cedicalCircleService;
    @Autowired
    private ImageUtils imageUtils;
    @Autowired
    private HealthActivityDetailRepository healthActivityDetailRepository;
    
    
    @RequestMapping(value = "/listdata", method = RequestMethod.POST)
    public JsonListResponseEntity<HealthActivityInfoDTO> searchActivity(@RequestBody String request) {
        JsonKeyReader reader = new JsonKeyReader(request);
        String status =  reader.readString("status", true);
        String title =  reader.readString("title", true);
        String onlineTime =  reader.readString("onlineTime", true);
        String offlineTime =  reader.readString("offlineTime", true);
        int flag =  reader.readDefaultInteger("flag", 1);
        int pageSize =  reader.readDefaultInteger("pageSize", 10);
        String mainArea =  reader.readDefaultString("mainArea", "3101").concat("00000000");
        JsonListResponseEntity<HealthActivityInfoDTO> entity = new JsonListResponseEntity<HealthActivityInfoDTO>();
        DicArea dicArea = dicAreaRepository.getAddress(mainArea);
        List<HealthActivityInfo> infos = infoService.getHealthActivityInfos(dicArea.getUpper_code() ,status, title, onlineTime, offlineTime,
                flag, pageSize);
        int count = infoService.getHealthActivityInfoCount(dicArea.getUpper_code(), status, title, onlineTime, offlineTime);
        List<HealthActivityInfoDTO> infoDTOs = HealthActivityInfoDTO.infoDTO(infos);
        entity.setContent(infoDTOs, infoDTOs.size() == 10, null, String.valueOf((flag + 1)));
        Map<String, Object> extras = new HashMap<String, Object>();
        int ps = count / pageSize;
        extras.put("total_pages", count % pageSize == 0 ? ps : ps + 1);
        extras.put("total_elements", count);
        entity.setExtras(extras);
        entity.setMsg("查询成功");
        return entity;
    }

    @RequestMapping("/findActivity")
    public JsonResponseEntity<HealthActivityInfoDTO> findActivitie(@RequestParam() String acitivityId) {
        JsonResponseEntity<HealthActivityInfoDTO> entity = new JsonResponseEntity<HealthActivityInfoDTO>();
        HealthActivityInfo info = activityRepo.findOne(acitivityId);
        HealthActivityInfoDTO infoDto = new HealthActivityInfoDTO(info);
        infoDto.setTotalApplied(healthActivityDetailRepository.findActivityRegistrationByActivityId(info.getActivityid()));// 已报名人数
        entity.setData(infoDto);
        entity.setMsg("查询成功");
        return entity;
    }

    @RequestMapping(value = "/saveActivity", method = RequestMethod.POST)
    public JsonResponseEntity<String> saveActivity(@RequestBody String request) {
        JsonResponseEntity<String> entity = new JsonResponseEntity<String>();
        JsonKeyReader reader = new JsonKeyReader(request);
        HealthActivityInfo info = new HealthActivityInfo();
        info.setActivityid(reader.readString("activityid", true));
        info.setHost(reader.readString("host", true));
        info.setTitle(reader.readString("title", true));
        info.setSpeaker(reader.readString("speaker", true));
        info.setDepartment(reader.readString("department", true));
        info.setPftitle(reader.readString("pftitle", true));
        info.setStarttime(DateFormatter.parseDateTime(reader.readString("starttime", false)));
        info.setEndtime(DateFormatter.parseDateTime(reader.readString("endtime", false)));
        info.setEnrollStartTime(DateFormatter.parseDateTime(reader.readString("enroll_start_time", false)));
        info.setEnrollEndTime(DateFormatter.parseDateTime(reader.readString("enroll_end_time", false)));
        info.setOfflineStartTime(DateFormatter.parseDateTime(reader.readString("offline_start_time", true)));
        info.setOfflineEndTime(DateFormatter.parseDateTime(reader.readString("offline_end_time", true)));
        info.setLocate(reader.readString("locate", true));
        info.setProvince(reader.readString("province", false));
        info.setCity(reader.readString("city", false));
        info.setCounty(reader.readString("county", true));
        info.setQuota(reader.readInteger("quota", true));
        info.setOnlineTime(DateFormatter.parseDateTime(reader.readString("online_time", false)));
        info.setOfflineTime(DateFormatter.parseDateTime(reader.readString("offline_time", false)));
        info.setSummary(reader.readString("summary", true));
        info.setSummaryHtml(reader.readString("summaryHtml", true));
        info.setPhoto(reader.readString("photo", true));
        info.setThumbnail(reader.readString("thumbnail", true));
        info.setDelFlag("0");
        info.setStyle(1);
        info.setUpdateDate(new Date());
        info.setIscancel("0");
        info.setUrl(reader.readString("url", true));
        if (info.getOnlineTime().after(new Date())) {
            info.setOnlineStatus("0");//未上线
        } else if (info.getOfflineTime().before(new Date())) {
            info.setOnlineStatus("2");//已下线
        } else {
            info.setOnlineStatus("1");//已上线
        }
        if (StringUtils.isEmpty(info.getActivityid())) {
            info.setActivityid(IdGen.uuid());
            info.setCreateDate(new Date());
            activityRepo.save(info);
            entity.setMsg("添加成功");
        } else {
            activityRepo.saveAndFlush(info);
            entity.setMsg("修改成功");
        }
        return entity;
    }

    /**
     * 根据activityId复制一个新活动
     * @param activityId
     * @return
     */
    @RequestMapping(value = "/copyActivity", method = RequestMethod.GET)
    public JsonResponseEntity<String> copyActivity(@RequestParam String activityId) {
        JsonResponseEntity<String> entity = new JsonResponseEntity<String>();
        HealthActivityInfo info = activityRepo.findOne(activityId);
        HealthActivityInfo saveInfo = new HealthActivityInfo();
        BeanUtils.copyProperties(info, saveInfo);
        saveInfo.setActivityid(IdGen.uuid());
        saveInfo.setTitle(info.getTitle() + "1");
        saveInfo.setUpdateDate(new Date());
        activityRepo.save(saveInfo);
        entity.setMsg("复制成功");
        return entity;
    }

    /**
     * 根据activityId删除一个活动
     * @param activityId
     * @return
     */
    @RequestMapping(value = "/deleteActivity", method = RequestMethod.DELETE)
    public JsonResponseEntity<String> deleteActivity(@RequestParam String activityId) {
        JsonResponseEntity<String> entity = new JsonResponseEntity<String>();
        String[] ids = activityId.split(",");
        for (String id : ids) {
            if(StringUtils.isEmpty(id)){
               continue; 
            }
            HealthActivityInfo info = activityRepo.findOne(id);
            info.setDelFlag("1");
            activityRepo.saveAndFlush(info);
        }
        entity.setMsg("删除成功");
        return entity;
    }

    /**
     * 根据活动activityId查询报名用户列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/userList", method = RequestMethod.POST)
    public JsonListResponseEntity<UserInfoDTO> userList(@RequestBody String request) {
        JsonKeyReader reader = new JsonKeyReader(request);
        String activityId =  reader.readString("activityId", true);
        int pageNum =  reader.readDefaultInteger("flag", 1);
        int pageSize =  reader.readDefaultInteger("pageSize", 10);
        JsonListResponseEntity<UserInfoDTO> entity = new JsonListResponseEntity<UserInfoDTO>();

        List<HealthActivityDetail> details = infoService.getHealthActivityDetailsByActivityId(activityId,pageNum, pageSize);

        int count = infoService.getHealthActivityDetailsCountByActivityId(activityId);
        List<UserInfoDTO> infoDTOs = UserInfoDTO.infoDTO(details);
        entity.setContent(infoDTOs, infoDTOs.size() == 10, null, String.valueOf((pageNum + 1)));
        Map<String, Object> extras = new HashMap<String, Object>();
        int ps = count / pageSize;
        extras.put("total_pages", count % pageSize == 0 ? ps : ps + 1);
        extras.put("total_elements", count);
        entity.setExtras(extras);
        entity.setMsg("查询成功");
        return entity;
    }
    
    /**
     * 查询省市区字段表数据
     * @param upperCode
     * @return JsonListResponseEntity<DicArea>
     */
    @RequestMapping(value = "/firstAddressInfo", method = RequestMethod.GET)
    public JsonListResponseEntity<DicArea> getFirstAddressInfo(@RequestParam(required = false) String upperCode) {
        JsonListResponseEntity<DicArea> entity = new JsonListResponseEntity<DicArea>();
        if(StringUtils.isEmpty(upperCode)){
            entity.setContent(dicAreaRepository.getAddressListByLevel("1"));
        }else{
            entity.setContent(dicAreaRepository.getAddressListByLevelAndFatherId(upperCode));
        }
        return entity;
    }

    /**
     * 医学圈子内容详情
     * @param screen_width
     * @param circle_id
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public JsonResponseEntity<MedicalCircleDetailAPIEntity> getCircleDetail(
            @RequestHeader(value = "screen-width", defaultValue = "100") String screen_width,
            @RequestParam(value = "circle_id", required = true) String circle_id,
            @RequestParam(value = "doctor_id", required = true) String doctor_id
            ) {
        JsonResponseEntity<MedicalCircleDetailAPIEntity> responseEntity = new JsonResponseEntity<>();
        MedicalCircle mc = mcService.getMedicalCircle(circle_id);
        responseEntity.setData(newMedicalCircleDetailAPIEntity(new MedicalCircleDependence(mcService, dictCache),
                mc, screen_width, doctor_id));
        mcService.view(circle_id, doctor_id);//redis
        return responseEntity;
    }
    
    public MedicalCircleDetailAPIEntity newMedicalCircleDetailAPIEntity(MedicalCircleDependence dep, MedicalCircle mc, String screen_width, String uid) {
        MedicalCircleDetailAPIEntity entity = new MedicalCircleDetailAPIEntity();
        if (dep == null && mc == null) {
            return entity;
        }
        DictCache dictCache = dep.getDictCache();
        MedicalCircleService mcService = dep.getMcService();
        Doctor doctor = getDoctorByDocotrId(mc.getDoctorid());
        if (doctor != null) {
            String circle_id = mc.getId();
            String doctor_id = doctor.getUid();
            entity.setDoctor_id(doctor_id);
            entity.setAgo(TimeAgoUtils.ago(mc.getSendtime()));
            entity.setAvatar(doctor.getAvatar());
            entity.setCircle_id(circle_id);
            entity.setCircle_type(mc.getType());
            entity.setComment_num(mcService.getCommentsNum(circle_id));
            entity.setLike_num(null != mc.getPraisenum() ? mc.getPraisenum() : 0);
                    entity.setHospital(doctor.getHospitalName());
            if (!StringUtils.isEmpty(uid)) {
                entity.setIs_liked(circleLikeUtils.isLikeOne(circle_id, uid));
            }
            entity.setName(doctor.getName());
            entity.setTag(dictCache.queryTagName(mc.getTagid()));
            entity.setColor(dictCache.queryTagColor(mc.getTagid()));
            entity.setViews(mcService.getCircleViews(circle_id));//redis
            entity.setIs_collected(mcService.checkCollect(circle_id, uid, 1));
            cedicalCircleService.getMedicalCircle(circle_id);
            List<Doctor> doctors = getDoctorByDocotrIds(circleLikeUtils.likeUserIds(circle_id));
            if(doctors != null && doctors.size() > 0){
                String[] docLikeNames = new String[doctors.size()];
                for (int i = 0; i < doctors.size(); i++) {
                    Doctor doc = doctors.get(i);
                    if(doc != null) {
                        String name = doc.getName();
                        if(StringUtils.isEmpty(name)){
                            name = doc.getNickname();
                        }
                        docLikeNames[i] = doc.getUid() + ":" + name;
                        entity.setLike_doc_names("");
                        if (i == doctors.size() - 1) {
                            entity.setLike_doc_names(entity.getLike_doc_names() + name);
                        } else {
                            entity.setLike_doc_names(entity.getLike_doc_names() + name + ",");
                        }
                    }
                }
                entity.setLiked_doc_name(docLikeNames);
            }
            Integer type = mc.getType();
            entity.setCircle_type(type);
            List<ArticleAttach> images = mcService.getCircleAttachs(mc.getId());
            List<ImageAPIEntity> imageAPIEntities = new ArrayList<>();
            if (images != null && images.size() > 0) {
                if (images.size() == 1) {
                    ImageAPIEntity imageAPIEntity = new ImageAPIEntity();
                    ImageUtils.Image image = imageUtils.getImage(images.get(0).getAttachid());
                    if (image != null) {
                        imageAPIEntity.setRatio(imageUtils.getImgRatio(image));
                        imageAPIEntity.setUrl(image.getUrl());
                        imageAPIEntity.setThumb(imageUtils.getBigThumb(image, screen_width));
                        imageAPIEntity.setHeight(imageUtils.getUsefulImgHeight(image, screen_width));
                        imageAPIEntity.setWidth(imageUtils.getUsefulImgWidth(image, screen_width));
                        imageAPIEntities.add(imageAPIEntity);
                    }
                } else {
                    for (ArticleAttach image : images) {
                        ImageAPIEntity imageAPIEntity = new ImageAPIEntity();
                        imageAPIEntity.setUrl(image.getAttachid());
                        imageAPIEntity.setThumb(imageUtils.getSquareThumb(image.getAttachid(), screen_width));
                        imageAPIEntities.add(imageAPIEntity);
                    }
                }
            }
            if (type == 1) {//帖子
                NoteAPIEntity note = new NoteAPIEntity();
                note.setContent(mc.getContent());
                note.setImages(imageAPIEntities);
                note.setTitle(mc.getTitle());
                entity.setNote(note);
            } else if (type == 2) {//病例
                CaseAPIEntity cases = new CaseAPIEntity();
                cases.setTitle(mc.getTitle());
                cases.setContent(mc.getContent());
                cases.setImages(imageAPIEntities);
                entity.setCases(cases);
            } else if (type == 3) {//动态
                DynamicAPIEntity dynamic = new DynamicAPIEntity();
                dynamic.setContent(mc.getContent());
                dynamic.setImages(imageAPIEntities);
                ArticleTransmit share = mcService.getMedicalCircleForward(mc.getId());
                if (share != null) {
                    ShareAPIEntity shareAPIEntity = new ShareAPIEntity();
                    shareAPIEntity.setTitle(share.getTitle());
                    shareAPIEntity.setDesc(share.getSubtitle());
                    shareAPIEntity.setThumb(share.getPic());
                    shareAPIEntity.setUrl(share.getUrl());
                    dynamic.setShare(shareAPIEntity);
                }
                entity.setDynamic(dynamic);
            }

            List<MedicalCircleCommunity> comments = mcService.getMedicalCircleComments(circle_id, "discusstime:asc",
                    new Date(0));
            List<CommentAPIEntity> commentlist = new ArrayList<CommentAPIEntity>();
            int cfloor = 1;
            for (MedicalCircleCommunity comment : comments) {
                CommentAPIEntity commentEntity = new CommentAPIEntity();
                Doctor commentDoctor =  getDoctorByDocotrId(comment.getDoctorid());
                if(commentDoctor==null){
                    continue;
                }
                commentEntity.setAgo(TimeAgoUtils.ago(comment.getDiscusstime()));
                commentEntity.setAvatar(commentDoctor.getAvatar());
                commentEntity.setContent(comment.getContent());
                commentEntity.setFloor(mcService.getFloor(cfloor));
                commentEntity.setDoctor_id(comment.getDoctorid());
                commentEntity.setName(commentDoctor.getName());

                int rfloor = 1;
                List<CommentAPIEntity> replyEntitylist = new ArrayList<CommentAPIEntity>();
                List<MedicalCircleReply> commentReplyList = mcService.getCommentReplyList(comment.getId(), new Date(0),
                        "discusstime:asc", 5);
                for (MedicalCircleReply reply : commentReplyList) {
                    CommentAPIEntity replyEntity = new CommentAPIEntity();
                    Doctor replyDoctor =  getDoctorByDocotrId(comment.getDoctorid());
                    if(replyDoctor == null){
                        continue;
                    }
                    replyEntity.setName(replyDoctor.getName());
                    replyEntity.setDoctor_id(reply.getDoctorid());
                    replyEntity.setAgo(TimeAgoUtils.ago(reply.getDiscusstime()));
                    replyEntity.setAvatar(replyDoctor.getAvatar());
                    replyEntity.setContent(reply.getContent());
                    replyEntity.setFloor(mcService.getFloor(rfloor));
                    rfloor++;
                    replyEntitylist.add(replyEntity);
                }
                if (commentReplyList.size() < 5) {
                    commentEntity.setReply_more(false);
                } else {
                    commentEntity.setReply_more(true);
                }
                cfloor++;
                commentEntity.setReply_list(replyEntitylist);
                commentlist.add(commentEntity);
            }
            entity.setComment_list(commentlist);

            ShareAPIEntity shareEntity = new ShareAPIEntity();
            String content = mc.getContent();
            if (!StringUtils.isEmpty(content) && content.length() > 100) {
                content = content.substring(0, 100) + "...";
            }
            shareEntity.setTitle(StringUtils.isEmpty(mc.getTitle()) ? "万达全程健康云" : mc.getTitle());
            shareEntity.setDesc(content);
            shareEntity.setThumb("http://img.wdjky.com/app/ic_launcher");
//            shareEntity.setUrl("http://10.1.64.194/neohealthcloud-doctor/api/medicalcircle/detail?circle_id=" + mc.getId() + "&doctor_id=" + mc.getDoctorid());
            entity.setShare(shareEntity);
        }
        return entity;
    }
    
    public Doctor getDoctorByDocotrId(String doctorId) {
        return docinfoService.findDoctorByUid(doctorId);
    }
    
    public List<Doctor> getDoctorByDocotrIds(String[] doctorIds) {
        if(doctorIds == null || doctorIds.length < 1){
            return null;
        }
        String ids = "";
        for (String id : doctorIds) {
            ids+=id + ",";
        }
        return docinfoService.findDoctorByIds(ids.substring(0, ids.length() - 1));
    }
    
    /**
	 * 报名活动
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/activities/participation", method = RequestMethod.POST)
	@VersionRange
	public JsonResponseEntity<String> doParticipationActivity(@RequestBody String request) {
	        
			JsonKeyReader reader = new JsonKeyReader(request);
			String activityid =  reader.readString("activityid", false);
			String registerId = reader.readString("uid", false);

			JsonResponseEntity<String> response = new JsonResponseEntity<String>();
			HealthActivityDetail detail = healthActivityDetailRepository.findActivityDetailByAidAndRid(activityid, registerId);
			if (detail == null || detail.getActivityid() == null){
				HealthActivityInfo info = activityRepo.findOne(activityid);
				
				Integer totalApply = healthActivityDetailRepository.findActivityRegistrationByActivityId(activityid);// 已报名人数
				Integer quota = info.getQuota();// 活动限定名额

				if (info.getEnrollStartTime().after(new Timestamp(System.currentTimeMillis()))) {
					response.setCode(1620);
					response.setMsg("活动报名尚未开始");
					return response;
				}else if (info.getEnrollEndTime().before(new Timestamp(System.currentTimeMillis()))) {
					response.setCode(1608);
					response.setMsg("活动报名已结束");
					return response;
				}else if (totalApply != null && Integer.valueOf(totalApply) >= quota) {
					response.setCode(1609);
					response.setMsg("名额已满");
					return response;

				}else {
					HealthActivityDetail detailInfo = new HealthActivityDetail();
                    detailInfo.setSigntime(DateFormatter.dateTimeFormat(new Date()));
                    detailInfo.setRegisterid(registerId);
                    detailInfo.setActivityid(activityid);
                    detailInfo.setId(IdGen.uuid());
                    detailInfo.setDelFlag("0");
                    healthActivityDetailRepository.save(detailInfo);
                    response.setCode(0);//1：报名成功
                    response.setMsg("报名成功");
                    
				}
			} else {
				response.setCode(1610);
				response.setMsg("不能重复报名");
				return response;
			}
		response.setMsg("报名成功");
			
		return response;
	}

	/**
	 * 取消活动报名
	 * @param activityid
	 * @return
	 */
	@RequestMapping(value = "/activities/participation", method = RequestMethod.DELETE)
	@VersionRange
	public JsonResponseEntity<String> doCancelParticipation(
			@RequestParam(value="uid",required=true) String registerId,
			@RequestParam(value = "activityid", required = true) String activityid) {


			JsonResponseEntity<String> response = new JsonResponseEntity<String>();
			HealthActivityDetail detail = healthActivityDetailRepository
					.findActivityDetailByAidAndRid(activityid, registerId);
			HealthActivityInfo info = activityRepo.findOne(activityid);
			
			if(detail == null){
			    response.setCode(1615);
                response.setMsg("请先报名");
                return response;
			}else if (info.getEndtime().before(new Timestamp(System.currentTimeMillis()))) {
				response.setCode(1616);
				response.setMsg("活动已结束不能取消报名");
				return response;
			}else if (info.getEnrollEndTime().before(new Timestamp(System.currentTimeMillis()))) {
                response.setCode(1617);
                response.setMsg("活动报名已结束");
                return response;
            }else {
				detail.setDelFlag("1");
				detail = healthActivityDetailRepository.save(detail);
				int result = detail.getDelFlag().equals("1") ? 1 : 0;// 0:取消报名失败，1：取消报名成功
				if(detail.getDelFlag().equals("1")){
					response.setCode(0);
					response.setMsg("取消报名成功");
				}else{
					response.setCode(1612);
					response.setMsg("取消报名失败");
				}
				return response;
			}
	}

}
