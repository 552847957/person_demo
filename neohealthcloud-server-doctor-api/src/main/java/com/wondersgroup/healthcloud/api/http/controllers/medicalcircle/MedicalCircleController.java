package com.wondersgroup.healthcloud.api.http.controllers.medicalcircle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.wondersgroup.common.image.utils.ImageUploader;
import com.wondersgroup.healthcloud.api.http.dto.doctor.DoctorAccountDTO;
import com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle.CaseAPIEntity;
import com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle.CommentAPIEntity;
import com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle.DocSearchResultAPIEntity;
import com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle.DoctorAPIEntity;
import com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle.DynamicAPIEntity;
import com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle.DynamicSearchResultAPIEntity;
import com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle.ImageAPIEntity;
import com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle.MedicalCircleAPIEntity;
import com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle.MedicalCircleDependence;
import com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle.MedicalCircleDetailAPIEntity;
import com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle.NoteAPIEntity;
import com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle.NoteCaseSearchResultAPIEntity;
import com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle.SearchResultAPIEntity;
import com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle.ShareAPIEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.dict.DictCache;
import com.wondersgroup.healthcloud.jpa.entity.circle.ArticleAttach;
import com.wondersgroup.healthcloud.jpa.entity.circle.ArticleTransmit;
import com.wondersgroup.healthcloud.jpa.entity.medicalcircle.MedicalCircle;
import com.wondersgroup.healthcloud.jpa.entity.medicalcircle.MedicalCircleAttention;
import com.wondersgroup.healthcloud.jpa.entity.medicalcircle.MedicalCircleCommunity;
import com.wondersgroup.healthcloud.jpa.entity.medicalcircle.MedicalCircleReply;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorAccountRepository;
import com.wondersgroup.healthcloud.services.doctor.DoctorService;
import com.wondersgroup.healthcloud.services.medicalcircle.MedicalCircleService;
import com.wondersgroup.healthcloud.utils.ImageUtils;
import com.wondersgroup.healthcloud.utils.TimeAgoUtils;
import com.wondersgroup.healthcloud.utils.circle.CircleLikeUtils;

@RestController
@RequestMapping("/api/medicalcircle")
public class MedicalCircleController {

    @Autowired
    private MedicalCircleService    mcService;

    @Autowired
    private DoctorService           docinfoService;

    @Autowired
    private DictCache               dictCache;

    @Autowired
    private DoctorAccountRepository doctorAccountRepository;

    private JsonListResponseEntity<MedicalCircleAPIEntity> getMedicalCircleList(String screen_width,
            Integer[] circle_type, String doctor_id, String uid, String order, String flag, Boolean collect) {

        JsonListResponseEntity<MedicalCircleAPIEntity> result = new JsonListResponseEntity<>();
        List<MedicalCircleAPIEntity> list = new ArrayList<>();
        Boolean more = true;
        Date sendtime = new Date();
        if (StringUtils.isNotEmpty(flag)) {
            sendtime = new Date(Long.valueOf(flag));
        }
        if (StringUtils.isEmpty(order)) {
            order = "sendtime:desc";
        }
        List<MedicalCircle> mcList;
        if (collect) {
            mcList = mcService.getCollectCircleList(doctor_id, sendtime, circle_type);
        } else {
            if (StringUtils.isNotEmpty(doctor_id)) {
                mcList = mcService.getUserMedicalCircle(doctor_id, circle_type, sendtime, order); //获取个人健康圈列表
            } else {
                mcList = mcService.getAllMedicalCircle(circle_type, order, sendtime); //获取全部健康圈列表
            }
        }

        for (MedicalCircle mc : mcList) {
            MedicalCircleAPIEntity entity = new MedicalCircleAPIEntity();
            DoctorAccountDTO doctorInfo = getDoctorInfo(mc.getDoctorid());
            if (doctorInfo == null) {
                continue;
            }
            entity.setAgo(TimeAgoUtils.ago(mc.getSendtime()));
            entity.setComment_num(mcService.getCommentsNum(mc.getId()));
            entity.setTag(dictCache.queryTagName(mc.getTagid()));
            entity.setColor(dictCache.queryTagColor(mc.getTagid()));
            entity.setAvatar(doctorInfo.getAvatar());
            entity.setCircle_id(mc.getId());
            entity.setDoctor_id(mc.getDoctorid());
            entity.setHospital(doctorInfo.getHospitalName());
            if (StringUtils.isNotEmpty(uid)) {
                entity.setIs_liked(CircleLikeUtils.isLikeOne(mc.getId(), uid));
            }
            entity.setLike_num(mc.getPraisenum());
            entity.setName(doctorInfo.getName());
            Integer type = mc.getType();
            entity.setCircle_type(type);
            List<ArticleAttach> images = mcService.getCircleAttachs(mc.getId());
            List<ImageAPIEntity> imageAPIEntities = new ArrayList<>();
            if (images != null && images.size() > 0) {
                if (images.size() == 1) {
                    ImageAPIEntity imageAPIEntity = new ImageAPIEntity();
                    ImageUtils.Image image = ImageUtils.getImage(images.get(0).getAttachid());
                    if (image != null) {
                        imageAPIEntity.setRatio(ImageUtils.getImgRatio(image));
                        imageAPIEntity.setUrl(image.getUrl());
                        imageAPIEntity.setThumb(ImageUtils.getBigThumb(image, screen_width));
                        imageAPIEntity.setHeight(ImageUtils.getUsefulImgHeight(image, screen_width));
                        imageAPIEntity.setWidth(ImageUtils.getUsefulImgWidth(image, screen_width));
                        imageAPIEntities.add(imageAPIEntity);
                    }
                } else {
                    for (ArticleAttach image : images) {
                        ImageAPIEntity imageAPIEntity = new ImageAPIEntity();
                        imageAPIEntity.setUrl(image.getAttachid());
                        imageAPIEntity.setThumb(ImageUtils.getSquareThumb(image.getAttachid(), screen_width));
                        imageAPIEntities.add(imageAPIEntity);
                    }
                }
            }
            String content = mc.getContent();
            if (StringUtils.isNotBlank(content) && content.length() > 100) {
                content = content.substring(0, 100) + "...";
            }
            if (type == 1) {//帖子
                NoteAPIEntity note = new NoteAPIEntity();
                note.setContent(content);
                if (images != null && images.size() > 0) {
                    note.setHas_images(true);
                } else {
                    note.setHas_images(false);
                }
                note.setTitle(mc.getTitle());
                entity.setNote(note);
            } else if (type == 2) {//病例
                CaseAPIEntity cases = new CaseAPIEntity();
                cases.setTitle(mc.getTitle());
                cases.setContent(content);
                cases.setImages(imageAPIEntities);
                entity.setCases(cases);
            } else if (type == 3) {//动态
                DynamicAPIEntity dynamic = new DynamicAPIEntity();
                dynamic.setContent(content);
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

            if (mcList.get(mcList.size() - 1).equals(mc)) {
                flag = String.valueOf(mc.getSendtime().getTime());
            }
            list.add(entity);
        }
        if (mcList.size() < 20) {
            more = false;
        }

        result.setContent(list, more, order, flag);
        return result;
    }

    /**
     * 医学圈子列表
     * @param screen_width
     * @param doctor_id
     * @param order
     * @param flag
     * @return
     */
    @RequestMapping(value = "allCircle", method = RequestMethod.GET)
    @VersionRange
    public JsonListResponseEntity<MedicalCircleAPIEntity> getAllCircle(
            @RequestHeader(value = "screen-width") String screen_width,
            @RequestParam(value = "doctor_id", required = false) String doctor_id,
            @RequestParam(value = "order", required = false) String order,
            @RequestParam(value = "flag", required = false) String flag) {
        
        return getMedicalCircleList(screen_width, new Integer[] { 1, 2, 3 }, null, doctor_id, order, flag, false);
    }

    /**
     * 医学圈子内容详情
     * @param screen_width
     * @param circle_id
     * @return
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<MedicalCircleDetailAPIEntity> getCircleDetail(
            @RequestHeader(value = "screen-width",defaultValue = "100") String screen_width,
            @RequestParam(value = "circle_id", required = true) String circle_id) {
        JsonResponseEntity<MedicalCircleDetailAPIEntity> responseEntity = new JsonResponseEntity<>();
        MedicalCircle mc = mcService.getMedicalCircle(circle_id);
        String uid = null;
        if (mc.getType() == 2) {
            responseEntity.setCode(1281);
            responseEntity.setMsg("未认证医生无法查看病例");
        } else {
            responseEntity.setData(new MedicalCircleDetailAPIEntity(new MedicalCircleDependence(mcService, dictCache),
                    mc, screen_width, uid));
            mcService.view(circle_id, uid);
        }
        return responseEntity;
    }

    /**
     * 评论列表
     * @param circle_id
     * @param flag
     * @return
     */
    @RequestMapping(value = "/comments", method = RequestMethod.GET)
    @VersionRange
    public JsonListResponseEntity<CommentAPIEntity> getCommentList(
            @RequestParam(value = "circle_id", required = true) String circle_id,
            @RequestParam(value = "order", required = false) String order,
            @RequestParam(value = "flag", required = false) String flag) {
        
        JsonListResponseEntity<CommentAPIEntity> responseEntity = new JsonListResponseEntity<>();
        List<CommentAPIEntity> commentAPIEntities = new ArrayList<>();
        Boolean more = true;
        Date discusstime = new Date(0l);
        if (StringUtils.isNotEmpty(flag)) {
            discusstime = new Date(Long.valueOf(flag));
        }
        if (StringUtils.isEmpty(order)) {
            order = "discusstime:asc";
        }

        List<MedicalCircleCommunity> comments = mcService.getMedicalCircleComments(circle_id, order, discusstime);
        int cfloor = 1;
        for (MedicalCircleCommunity comment : comments) {
            CommentAPIEntity commentEntity = new CommentAPIEntity();
            DoctorAccountDTO doctorInfo = getDoctorInfo(comment.getDoctorid());
            if (doctorInfo == null) {
                continue;
            }
            commentEntity.setAgo(TimeAgoUtils.ago(comment.getDiscusstime()));
            commentEntity.setAvatar(doctorInfo.getAvatar());
            commentEntity.setContent(comment.getContent());
            commentEntity.setFloor(mcService.getFloor(cfloor));
            commentEntity.setDoctor_id(comment.getDoctorid());
            commentEntity.setName(doctorInfo.getName());
            commentEntity.setComment_id(comment.getId());
            int rfloor = 1;
            List<CommentAPIEntity> replyEntitylist = new ArrayList<CommentAPIEntity>();
            List<MedicalCircleReply> commentReplyList = mcService.getCommentReplyList(comment.getId(), new Date(0),
                    "discusstime:asc", 5);
            for (MedicalCircleReply reply : commentReplyList) {
                CommentAPIEntity replyEntity = new CommentAPIEntity();
                DoctorAccountDTO doctor = getDoctorInfo(reply.getDoctorid());
                if (doctor == null) {
                    continue;
                }
                replyEntity.setName(doctor.getName());
                replyEntity.setDoctor_id(reply.getDoctorid());
                replyEntity.setAgo(TimeAgoUtils.ago(reply.getDiscusstime()));
                replyEntity.setAvatar(doctor.getAvatar());
                replyEntity.setContent(reply.getContent());
                replyEntity.setFloor(mcService.getFloor(rfloor));
                DoctorAccountDTO replyDoctor = getDoctorInfo(reply.getReplyid());
                if (replyDoctor != null) {
                    replyEntity.setReply_name(replyDoctor.getName() != null ? replyDoctor.getName() : replyDoctor
                            .getNickname());
                }
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
            if (comment.equals(comments.get(comments.size() - 1))) {
                flag = String.valueOf(comment.getDiscusstime().getTime());
            }
            commentAPIEntities.add(commentEntity);
        }
        if (comments.size() < 20) {
            more = false;
        }
        responseEntity.setContent(commentAPIEntities, more, order, flag);
        return responseEntity;
    }

    /**
     * 回复列表
     * @param comment_id
     * @param flag
     * @return
     */
    @RequestMapping(value = "/comments/reply", method = RequestMethod.GET)
    @VersionRange
    public JsonListResponseEntity<CommentAPIEntity> getCommentReplyList(
            @RequestParam(value = "comment_id") String comment_id,
            @RequestParam(value = "order", required = false) String order,
            @RequestParam(value = "flag", required = false) String flag) {
        
        JsonListResponseEntity<CommentAPIEntity> responseEntity = new JsonListResponseEntity<>();
        Boolean more = true;
        Date discusstime = new Date(0l);
        if (StringUtils.isNotEmpty(flag)) {
            discusstime = new Date(Long.valueOf(flag));
        }
        if (StringUtils.isEmpty(order)) {
            order = "discusstime:asc";
        }
        int rfloor = 1;
        List<CommentAPIEntity> replyEntitylist = new ArrayList<>();
        List<MedicalCircleReply> commentReplyList = mcService.getCommentReplyList(comment_id, discusstime, order, 20);
        for (MedicalCircleReply reply : commentReplyList) {
            CommentAPIEntity replyEntity = new CommentAPIEntity();
            DoctorAccountDTO doctorInfo = getDoctorInfo(reply.getDoctorid());
            if (doctorInfo == null) {
                continue;
            }
            replyEntity.setName(doctorInfo.getName());
            replyEntity.setDoctor_id(reply.getDoctorid());
            replyEntity.setAgo(TimeAgoUtils.ago(reply.getDiscusstime()));
            replyEntity.setAvatar(doctorInfo.getAvatar());
            replyEntity.setContent(reply.getContent());
            replyEntity.setFloor(mcService.getFloor(rfloor));
            DoctorAccountDTO replyDoctorInfo = getDoctorInfo(reply.getReplyid());
            if (replyDoctorInfo != null) {
                replyEntity.setReply_name(replyDoctorInfo.getName() != null ? replyDoctorInfo.getName()
                        : replyDoctorInfo.getNickname());
            }
            rfloor++;
            replyEntitylist.add(replyEntity);
            if (reply.equals(commentReplyList.get(commentReplyList.size() - 1))) {
                flag = String.valueOf(reply.getDiscusstime().getTime());
            }
        }
        if (commentReplyList.size() < 20) {
            more = false;
        }
        responseEntity.setContent(replyEntitylist, more, order, flag);
        return responseEntity;
    }

    /**
     * 发布
     * @param doctor_id
     * @param circle_type
     * @param title
     * @param content
     * @param images
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/publish", method = RequestMethod.POST)
    @VersionRange
    public JsonResponseEntity<String> publish(@RequestParam("doctor_id") String doctor_id,
            @RequestParam("circle_type") Integer circle_type,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "content", required = false) String content,
            @RequestPart(value = "images", required = false) MultipartFile[] images) {
       
        JsonResponseEntity<String> entity = new JsonResponseEntity<>();
        //        if (!SensitiveWordsFilterUtils.isIncludeSenstiveWords(content)) {
        try {
            List<String> imageURLs = Lists.newArrayList();
            for (MultipartFile image : images) {
                imageURLs.add(ImageUploader.upload("app", IdGen.uuid() + ".jpg", image.getBytes()));

            }
            mcService.publish(doctor_id, title, content, circle_type, imageURLs);
            entity.setMsg("发布成功");
            //        } else {
            //            entity.setCode(1299);
            //            entity.setMsg("内容含有敏感词汇");
            //        }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return entity;
    }

    /**
     * 评论
     * @param body
     * @return
     */
    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    @VersionRange
    public JsonResponseEntity<CommentAPIEntity> comment(@RequestHeader("access-token") String token,
            @RequestBody String body) {
        
        JsonResponseEntity<CommentAPIEntity> entity = new JsonResponseEntity<>();
        JsonKeyReader reader = new JsonKeyReader(body);
        String doctor_id = reader.readString("doctor_id", false);
        String content = reader.readString("content", false);
        String circle_id = reader.readString("circle_id", false);
        //                if (!SensitiveWordsFilterUtils.isIncludeSenstiveWords(content)) {
        MedicalCircleCommunity comment = mcService.comment(doctor_id, circle_id, content);
        CommentAPIEntity commentEntity = new CommentAPIEntity();
        DoctorAccountDTO doctorInfo = getDoctorInfo(doctor_id);
        if (doctorInfo != null) {
            commentEntity.setName(doctorInfo.getName());
            commentEntity.setAvatar(doctorInfo.getAvatar());
        }
        commentEntity.setAgo(TimeAgoUtils.ago(comment.getDiscusstime()));
        commentEntity.setContent(comment.getContent());
        commentEntity.setDoctor_id(comment.getDoctorid());
        commentEntity.setComment_id(comment.getId());
        entity.setData(commentEntity);
        entity.setMsg("评论成功");
        //                } else {
        //                    entity.setCode(1299);
        //                    entity.setMsg("内容含有敏感词汇");
        //                }
        return entity;
    }

    /**
     * 回复
     * @param body
     * @return
     */
    @RequestMapping(value = "/comment/reply", method = RequestMethod.POST)
    @VersionRange
    public JsonResponseEntity<CommentAPIEntity> reply(@RequestHeader("access-token") String token,
            @RequestBody String body) {
       
        JsonResponseEntity<CommentAPIEntity> entity = new JsonResponseEntity<>();
        JsonKeyReader reader = new JsonKeyReader(body);
        String doctor_id = reader.readString("doctor_id", false);
        String comment_id = reader.readString("comment_id", false);
        String reply_doctor_id = reader.readString("reply_doctor_id", false);
        String content = reader.readString("content", false);
        //                if (!SensitiveWordsFilterUtils.isIncludeSenstiveWords(content)) {
        mcService.reply(comment_id, doctor_id, reply_doctor_id, content);
        entity.setMsg("回复成功");
        CommentAPIEntity replyEntity = new CommentAPIEntity();
        DoctorAccountDTO replyDoctorInfo = getDoctorInfo(doctor_id);
        if (replyDoctorInfo != null) {
            replyEntity.setName(replyDoctorInfo.getName());
            replyEntity.setAvatar(replyDoctorInfo.getAvatar());
        }
        replyEntity.setDoctor_id(doctor_id);
        replyEntity.setAgo("刚刚");
        replyEntity.setContent(content);
        DoctorAccountDTO doctorInfo = getDoctorInfo(reply_doctor_id);
        if (doctorInfo != null) {
            replyEntity.setReply_name(doctorInfo.getName() != null ? doctorInfo.getName() : doctorInfo.getNickname());
        }
        entity.setData(replyEntity);
        //                } else {
        //                    entity.setCode(1299);
        //                    entity.setMsg("内容含有敏感词汇");
        //                }
        return entity;
    }

    /**
     * 点赞
     * @param body
     * @return
     */
    @RequestMapping(value = "/like", method = RequestMethod.POST)
    @VersionRange
    public JsonResponseEntity<String> like(@RequestBody String body) {
       
        JsonResponseEntity<String> entity = new JsonResponseEntity<>();
        JsonKeyReader reader = new JsonKeyReader(body);
        String doctor_id = reader.readString("doctor_id", false);
        String circle_id = reader.readString("circle_id", false);
        Boolean success = mcService.like(doctor_id, circle_id);
        if (success) {
            entity.setMsg("点赞成功");
            DoctorAccountDTO doctor = getDoctorInfo(doctor_id);
            if (doctor != null) {
                entity.setData(doctor_id + ":" + doctor.getName() != null ? doctor.getName() : doctor.getNickname());
            }
        } else {
            entity.setCode(1320);
            entity.setMsg("点赞失败");
        }
        return entity;
    }

    /**
     * 取消点赞
     * @param body
     * @return
     */
    @RequestMapping(value = "/unlike", method = RequestMethod.POST)
    @VersionRange
    public JsonResponseEntity<String> unlike(@RequestBody String body) {
       
        JsonResponseEntity<String> entity = new JsonResponseEntity<>();
        JsonKeyReader reader = new JsonKeyReader(body);
        String doctor_id = reader.readString("doctor_id", false);
        String circle_id = reader.readString("circle_id", false);
        Boolean success = mcService.unlike(doctor_id, circle_id);
        if (success) {
            entity.setMsg("取消点赞成功");
            DoctorAccountDTO doctor = getDoctorInfo(doctor_id);
            if (doctor != null) {
                entity.setData(doctor_id + ":" + doctor.getName() != null ? doctor.getName() : doctor.getNickname());
            }
        } else {
            entity.setCode(1320);
            entity.setMsg("取消点赞失败");
        }
        return entity;
    }

    /**
     * 分享
     * @param body
     * @return
     */
    @RequestMapping(value = "/forward", method = RequestMethod.POST)
    @VersionRange
    public JsonResponseEntity<String> forward(@RequestBody String body) {
        
        JsonResponseEntity<String> entity = new JsonResponseEntity<>();
        JsonKeyReader reader = new JsonKeyReader(body);
        String doctor_id = reader.readString("doctor_id", false);
        String content = reader.readString("content", true);
        String title = reader.readString("title", false);
        String desc = reader.readString("desc", false);
        String url = reader.readString("url", false);
        String thumb = reader.readString("thumb", true);
        mcService.forward(doctor_id, title, desc, thumb, url, content);
        entity.setMsg("分享成功");
        return entity;
    }

    /**
     * 举报
     * @param body
     * @return
     */
    @RequestMapping(value = "/report", method = RequestMethod.POST)
    @VersionRange
    public JsonResponseEntity<String> report(@RequestBody String body) {
        
        JsonResponseEntity<String> entity = new JsonResponseEntity<>();
        JsonKeyReader reader = new JsonKeyReader(body);
        String uid = reader.readString("doctor_id", false);
        String reportid = reader.readString("reportid", false);
        Integer content_type = reader.readInteger("content_type", false);
        Integer reporttype = reader.readInteger("report_type", false);
        Boolean success = mcService.report(uid, reportid, content_type, reporttype);
        if (success) {
            entity.setMsg("举报成功");
        } else {
            entity.setCode(1320);
            entity.setMsg("已举报，请不要重复举报");
        }
        return entity;
    }

    /**
     * 删除帖子
     * @return
     */
    @RequestMapping(method = RequestMethod.DELETE)
    @VersionRange
    public JsonResponseEntity<String> delCircle(@RequestParam("doctor_id") String doctor_id,
            @RequestParam("circle_id") String circle_id) {
        
        JsonResponseEntity<String> entity = new JsonResponseEntity<>();
        Boolean success = mcService.delMedicalCircle(doctor_id, circle_id);
        if (success) {
            entity.setMsg("删除成功");
        } else {
            entity.setCode(1320);
            entity.setMsg("删除失败");
        }
        return entity;
    }

    /**
     * 删除评论
     * @return
     */
    @RequestMapping(value = "/comment", method = RequestMethod.DELETE)
    @VersionRange
    public JsonResponseEntity<String> delComment(@RequestParam("doctor_id") String doctor_id,
            @RequestParam("comment_id") String comment_id) {
        
        JsonResponseEntity<String> entity = new JsonResponseEntity<>();
        Boolean success = mcService.delComment(doctor_id, comment_id);
        if (success) {
            entity.setMsg("删除成功");
        } else {
            entity.setCode(1320);
            entity.setMsg("删除失败");
        }
        return entity;
    }

    /**
     * 删除回复
     * @return
     */
    @RequestMapping(value = "/comment/reply", method = RequestMethod.DELETE)
    @VersionRange
    public JsonResponseEntity<String> delReply(@RequestParam("doctor_id") String doctor_id,
            @RequestParam("reply_id") String reply_id) {
        
        JsonResponseEntity<String> entity = new JsonResponseEntity<>();
        Boolean success = mcService.delReply(reply_id, doctor_id);
        if (success) {
            entity.setMsg("删除成功");
        } else {
            entity.setCode(1320);
            entity.setMsg("删除失败");
        }
        return entity;
    }

    /**
     * 搜索
     * @param query
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<SearchResultAPIEntity> search(@RequestParam("query") String query) {

        JsonResponseEntity<SearchResultAPIEntity> entity = new JsonResponseEntity<SearchResultAPIEntity>();
        SearchResultAPIEntity searchResultAPIEntity = new SearchResultAPIEntity();

        List<DocSearchResultAPIEntity> docSearchResultAPIEntities = new ArrayList<>();
        DocSearchResultAPIEntity doc = new DocSearchResultAPIEntity();
        doc.setName("张大锤");
        doc.setAvatar("http://qiniu/111.jpg");
        doc.setIs_attention(false);
        doc.setHospital("中山医院");
        docSearchResultAPIEntities.add(doc);

        List<NoteCaseSearchResultAPIEntity> noteCaseSearchResultAPIEntities = new ArrayList<NoteCaseSearchResultAPIEntity>();
        NoteCaseSearchResultAPIEntity notecase = new NoteCaseSearchResultAPIEntity();
        notecase.setName("张大锤");
        notecase.setHospital("中山医院");
        notecase.setAvatar("http://qiniu/111.jpg");
        notecase.setAgo("1小时前");
        notecase.setCircle_id("11111");
        notecase.setCircle_type(2);
        notecase.setComment_num(11l);
        notecase.setDoctor_id("2222");
        notecase.setLike_num(111l);
        notecase.setTag("推荐阅读");
        CaseAPIEntity case1 = new CaseAPIEntity();
        case1.setTitle("CT和MRI结果");
        case1.setContent("病例病例病例病例病例病例病例病例");
        List<ImageAPIEntity> imageAPIEntities = new ArrayList<>();
        ImageAPIEntity imageAPIEntity = new ImageAPIEntity();
        imageAPIEntity.setRatio(1.0f);
        imageAPIEntity.setUrl("http://qiniu/111.jgp");
        imageAPIEntity.setThumb("http://qiniu/111.jgp");
        imageAPIEntity.setHeight(320);
        imageAPIEntity.setWidth(240);
        imageAPIEntities.add(imageAPIEntity);
        case1.setImages(imageAPIEntities);
        notecase.setCases(case1);
        noteCaseSearchResultAPIEntities.add(notecase);

        List<DynamicSearchResultAPIEntity> dynamicSearchResultAPIEntities = new ArrayList<DynamicSearchResultAPIEntity>();
        DynamicSearchResultAPIEntity entity3 = new DynamicSearchResultAPIEntity();
        entity3.setAgo("3天前");
        entity3.setAvatar("http://qiniu.com/11.jpg");
        entity3.setCircle_id("3");
        entity3.setComment_num(3l);
        entity3.setLike_num(1l);
        entity3.setHospital("瑞金医院");
        entity3.setName("张医生");
        entity3.setTag("经验之谈");
        DynamicAPIEntity dynamic = new DynamicAPIEntity();
        dynamic.setContent("动态动态动态动态动态动态动态");
        dynamic.setImages(imageAPIEntities);
        ShareAPIEntity share = new ShareAPIEntity();
        share.setTitle("分享标题1");
        share.setDesc("分享内容。。。。。");
        share.setThumb("http://qiniu.com/11.jpg");
        share.setUrl("http://qiniu.com/11.jpg");
        dynamic.setShare(share);
        entity3.setDynamic(dynamic);
        dynamicSearchResultAPIEntities.add(entity3);

        searchResultAPIEntity.setDoc_list(docSearchResultAPIEntities);
        searchResultAPIEntity.setDoc_more(false);
        searchResultAPIEntity.setDynamic_list(dynamicSearchResultAPIEntities);
        searchResultAPIEntity.setDynamic_more(false);
        searchResultAPIEntity.setNotecase_list(noteCaseSearchResultAPIEntities);
        searchResultAPIEntity.setNotecase_more(true);

        entity.setData(searchResultAPIEntity);
        return entity;
    }

    /**
     * 搜索(医生列表-加载更多)
     * @param query
     * @return
     */
    @RequestMapping(value = "/search/doctor", method = RequestMethod.GET)
    @VersionRange
    public JsonListResponseEntity<DocSearchResultAPIEntity> searchDoc(@RequestParam("query") String query,
            @RequestParam(value = "order", required = false) String order,
            @RequestParam(value = "flag", required = false) String flag) {

        JsonListResponseEntity<DocSearchResultAPIEntity> entity = new JsonListResponseEntity<>();

        List<DocSearchResultAPIEntity> docSearchResultAPIEntities = new ArrayList<>();
        DocSearchResultAPIEntity doc = new DocSearchResultAPIEntity();
        doc.setName("张大锤");
        doc.setAvatar("http://qiniu/111.jpg");
        doc.setIs_attention(false);
        doc.setHospital("中山医院");
        docSearchResultAPIEntities.add(doc);

        entity.setContent(docSearchResultAPIEntities);
        return entity;
    }

    /**
     * 搜索(帖子/病例-加载更多)
     * @param query
     * @return
     */
    @RequestMapping(value = "/search/notecase", method = RequestMethod.GET)
    @VersionRange
    public JsonListResponseEntity<NoteCaseSearchResultAPIEntity> searchNotecase(@RequestParam("query") String query,
            @RequestParam(value = "order", required = false) String order,
            @RequestParam(value = "flag", required = false) String flag) {

        JsonListResponseEntity<NoteCaseSearchResultAPIEntity> entity = new JsonListResponseEntity<>();

        List<NoteCaseSearchResultAPIEntity> noteCaseSearchResultAPIEntities = new ArrayList<>();
        NoteCaseSearchResultAPIEntity notecase = new NoteCaseSearchResultAPIEntity();
        notecase.setName("张大锤");
        notecase.setHospital("中山医院");
        notecase.setAvatar("http://qiniu/111.jpg");
        notecase.setAgo("1小时前");
        notecase.setCircle_id("11111");
        notecase.setCircle_type(2);
        notecase.setComment_num(11l);
        notecase.setDoctor_id("2222");
        notecase.setLike_num(111l);
        notecase.setTag("推荐阅读");
        CaseAPIEntity case1 = new CaseAPIEntity();
        case1.setTitle("CT和MRI结果");
        case1.setContent("病例病例病例病例病例病例病例病例");
        List<ImageAPIEntity> imageAPIEntities = new ArrayList<>();
        ImageAPIEntity imageAPIEntity = new ImageAPIEntity();
        imageAPIEntity.setRatio(1.0f);
        imageAPIEntity.setUrl("http://qiniu/111.jgp");
        imageAPIEntity.setThumb("http://qiniu/111.jgp");
        imageAPIEntity.setHeight(320);
        imageAPIEntity.setWidth(240);
        imageAPIEntities.add(imageAPIEntity);
        case1.setImages(imageAPIEntities);
        notecase.setCases(case1);
        noteCaseSearchResultAPIEntities.add(notecase);

        entity.setContent(noteCaseSearchResultAPIEntities);
        return entity;
    }

    /**
     * 搜索(动态-加载更多)
     * @param query
     * @return
     */
    @RequestMapping(value = "/search/dynamic", method = RequestMethod.GET)
    @VersionRange
    public JsonListResponseEntity<DynamicSearchResultAPIEntity> searchDynamic(@RequestParam("query") String query,
            @RequestParam(value = "order", required = false) String order,
            @RequestParam(value = "flag", required = false) String flag) {

        JsonListResponseEntity<DynamicSearchResultAPIEntity> entity = new JsonListResponseEntity<>();

        List<ImageAPIEntity> imageAPIEntities = new ArrayList<>();
        ImageAPIEntity imageAPIEntity = new ImageAPIEntity();
        imageAPIEntity.setRatio(1.0f);
        imageAPIEntity.setUrl("http://qiniu/111.jgp");
        imageAPIEntity.setThumb("http://qiniu/111.jgp");
        imageAPIEntity.setHeight(320);
        imageAPIEntity.setWidth(240);
        imageAPIEntities.add(imageAPIEntity);

        List<DynamicSearchResultAPIEntity> dynamicSearchResultAPIEntities = new ArrayList<DynamicSearchResultAPIEntity>();
        DynamicSearchResultAPIEntity entity3 = new DynamicSearchResultAPIEntity();
        entity3.setAgo("3天前");
        entity3.setAvatar("http://qiniu.com/11.jpg");
        entity3.setCircle_id("3");
        entity3.setComment_num(3l);
        entity3.setLike_num(1l);
        entity3.setHospital("瑞金医院");
        entity3.setName("张医生");
        entity3.setTag("经验之谈");
        DynamicAPIEntity dynamic = new DynamicAPIEntity();
        dynamic.setContent("动态动态动态动态动态动态动态");
        dynamic.setImages(imageAPIEntities);
        ShareAPIEntity share = new ShareAPIEntity();
        share.setTitle("分享标题1");
        share.setDesc("分享内容。。。。。");
        share.setThumb("http://qiniu.com/11.jpg");
        share.setUrl("http://qiniu.com/11.jpg");
        dynamic.setShare(share);
        entity3.setDynamic(dynamic);
        dynamicSearchResultAPIEntities.add(entity3);

        entity.setContent(dynamicSearchResultAPIEntities);
        return entity;
    }

    /**
     * 收藏
     */
    @RequestMapping(value = "/collect", method = RequestMethod.POST)
    @VersionRange
    public JsonResponseEntity<String> collect(@RequestBody String body) {

        JsonResponseEntity<String> result = new JsonResponseEntity<>();
        JsonKeyReader reader = new JsonKeyReader(body);
        String doctor_id = reader.readString("doctor_id", false);
        String circle_id = reader.readString("circle_id", false);
        Boolean collect = mcService.collect(circle_id, doctor_id, 1);
        if (collect) {
            result.setMsg("收藏成功");
        } else {
            result.setCode(1320);
            result.setMsg("已收藏过");
        }
        return result;
    }

    /**
     * 取消收藏
     */
    @RequestMapping(value = "/cancelCollect", method = RequestMethod.POST)
    @VersionRange
    public JsonResponseEntity<String> collectDel(@RequestBody String body) {

        JsonResponseEntity<String> result = new JsonResponseEntity<>();
        JsonKeyReader reader = new JsonKeyReader(body);
        String doctor_id = reader.readString("doctor_id", false);
        String circle_id = reader.readString("circle_id", false);
        Boolean success = mcService.delCollect(circle_id, doctor_id, 1);
        if (success) {
            result.setCode(0);
            result.setMsg("取消收藏成功");
        } else {
            result.setCode(1321);
            result.setMsg("取消收藏失败");
        }
        return result;
    }

    /**
     * 关注
     */
    @RequestMapping(value = "/doctor/follow", method = RequestMethod.POST)
    @VersionRange
    public JsonResponseEntity<String> follow(@RequestBody String body) {

        JsonResponseEntity<String> result = new JsonResponseEntity<>();
        JsonKeyReader reader = new JsonKeyReader(body);
        String attention_id = reader.readString("attention_id", false);
        String followed_id = reader.readString("followed_id", false);
        Boolean success = mcService.attention(attention_id, followed_id);
        if (success) {
            result.setMsg("关注成功");
        } else {
            result.setCode(1320);
            result.setMsg("已关注过");
        }
        return result;
    }

    /**
     * 取消关注
     */
    @RequestMapping(value = "/doctor/unfollow", method = RequestMethod.POST)
    @VersionRange
    public JsonResponseEntity<String> unfollow(@RequestBody String body) {

        JsonResponseEntity<String> result = new JsonResponseEntity<>();
        JsonKeyReader reader = new JsonKeyReader(body);
        String attention_id = reader.readString("attention_id", false);
        String followed_id = reader.readString("followed_id", false);
        Boolean success = mcService.cancelAttention(attention_id, followed_id);
        if (success) {
            result.setMsg("取消关注成功");
        } else {
            result.setCode(1320);
            result.setMsg("未关注");
        }
        return result;
    }

    /**
     * 医生详情
     * @param uid
     * @param doctor_id
     * @return
     */
    @RequestMapping(value = "/doctor", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<DoctorAPIEntity> doctorinfo(@RequestParam("uid") String uid,
            @RequestParam("doctor_id") String doctor_id) {
        
        JsonResponseEntity<DoctorAPIEntity> result = new JsonResponseEntity<>();
        DoctorAPIEntity doctor = new DoctorAPIEntity();
        DoctorAccountDTO d = getDoctorInfo(doctor_id);
        if (d != null) {
            doctor.setDoctor_id(doctor_id);
            doctor.setAvatar(d.getAvatar());
            doctor.setHospital(d.getHospitalName());
            doctor.setName(d.getName());
            if (!uid.equals(doctor_id)) {
                doctor.setIs_attention(mcService.isAttention(uid, doctor_id));
            }
            doctor.setAttention_num(mcService.getDocFollowedNum(doctor_id));
            doctor.setFans_num(mcService.getDocFansNum(doctor_id));
            doctor.setNotecase_num(mcService.getNoteCaseNum(doctor_id));
            doctor.setDynamic_num(mcService.getDynamicNum(doctor_id));
        }
        result.setData(doctor);
        return result;
    }

    /**
     * 某医生帖子/病例列表
     * @param screen_width
     * @param doctor_id
     * @param order
     * @param flag
     * @return
     */
    @RequestMapping(value = "/doctor/notecase", method = RequestMethod.GET)
    @VersionRange
    public JsonListResponseEntity<MedicalCircleAPIEntity> getOneNoteCaseCircle(
            @RequestHeader(value = "screen-width") String screen_width,
            @RequestParam(value = "doctor_id", required = false) String doctor_id,
            @RequestParam(value = "uid", required = false) String uid,
            @RequestParam(value = "collect") Boolean collect,
            @RequestParam(value = "order", required = false) String order,
            @RequestParam(value = "flag", required = false) String flag) {

        return getMedicalCircleList(screen_width, new Integer[] { 1, 2 }, doctor_id, uid, order, flag, collect);
    }

    /**
     * 某医生动态列表
     * @param screen_width
     * @param doctor_id
     * @param order
     * @param flag
     * @return
     */
    @RequestMapping(value = "/doctor/dynamic", method = RequestMethod.GET)
    @VersionRange
    public JsonListResponseEntity<MedicalCircleAPIEntity> getOneDynamicCircle(
            @RequestHeader(value = "screen-width") String screen_width,
            @RequestParam(value = "doctor_id", required = false) String doctor_id,
            @RequestParam(value = "uid", required = false) String uid,
            @RequestParam(value = "collect") Boolean collect,
            @RequestParam(value = "order", required = false) String order,
            @RequestParam(value = "flag", required = false) String flag) {

        return getMedicalCircleList(screen_width, new Integer[] { 3 }, doctor_id, uid, order, flag, collect);
    }

    /**
     * 某医生关注列表
     * @param doctor_id
     * @param order
     * @param flag
     * @return
     */
    @RequestMapping(value = "/doctor/attention", method = RequestMethod.GET)
    @VersionRange
    public JsonListResponseEntity<DoctorAPIEntity> getOneAttentionList(@RequestParam("uid") String uid,
            @RequestParam(value = "doctor_id") String doctor_id,
            @RequestParam(value = "order", required = false) String order,
            @RequestParam(value = "flag", required = false) String flag) {
       
        JsonListResponseEntity<DoctorAPIEntity> result = new JsonListResponseEntity();
        Boolean more = true;
        Date attentiontime = new Date();
        if (StringUtils.isNotEmpty(flag)) {
            attentiontime = new Date(Long.valueOf(flag));
        }
        if (StringUtils.isEmpty(order)) {
            order = "attentiontime:desc";
        }
        List<MedicalCircleAttention> attlist = mcService.getDocFollowedList(doctor_id, order, attentiontime);
        List<DoctorAPIEntity> list = new ArrayList<>();
        for (MedicalCircleAttention att : attlist) {
            DoctorAPIEntity entity = new DoctorAPIEntity();
            DoctorAccountDTO d = getDoctorInfo(att.getConcernedid());
            if (d == null) {
                continue;
            }
            entity.setDoctor_id(att.getConcernedid());
            entity.setAvatar(d.getAvatar());
            entity.setHospital(d.getHospitalName());
            entity.setName(d.getName());
            entity.setIs_attention(mcService.isAttention(uid, d.getUid()));
            list.add(entity);
        }
        if (attlist.size() < 20) {
            more = false;
        } else {
            flag = String.valueOf(attlist.get(attlist.size() - 1).getAttentiontime().getTime());
        }
        result.setContent(list, more, order, flag);
        return result;
    }

    /**
     * 某医生粉丝列表
     * @param doctor_id
     * @param order
     * @param flag
     * @return
     */
    @RequestMapping(value = "/doctor/fans", method = RequestMethod.GET)
    @VersionRange
    public JsonListResponseEntity<DoctorAPIEntity> getOneFansList(@RequestParam("uid") String uid,
            @RequestParam(value = "doctor_id") String doctor_id,
            @RequestParam(value = "order", required = false) String order,
            @RequestParam(value = "flag", required = false) String flag) {

        JsonListResponseEntity<DoctorAPIEntity> result = new JsonListResponseEntity();
        Boolean more = true;
        Date attentiontime = new Date();
        if (StringUtils.isNotEmpty(flag)) {
            attentiontime = new Date(Long.valueOf(flag));
        }
        if (StringUtils.isEmpty(order)) {
            order = "attentiontime:desc";
        }
        List<MedicalCircleAttention> attlist = mcService.getDocFansList(doctor_id, order, attentiontime);
        List<DoctorAPIEntity> list = new ArrayList<>();
        for (MedicalCircleAttention att : attlist) {
            DoctorAPIEntity entity = new DoctorAPIEntity();
            DoctorAccountDTO d = getDoctorInfo(att.getDoctorid());
            if (d == null) {
                continue;
            }
            entity.setDoctor_id(att.getDoctorid());
            entity.setAvatar(d.getAvatar());
            entity.setHospital(d.getHospitalName());
            entity.setName(d.getName());
            entity.setIs_attention(mcService.isAttention(uid, d.getUid()));
            list.add(entity);
        }
        //获取最后一个对象的时间作为flag
        flag = String.valueOf(attlist.get(attlist.size() - 1) == null ? "" : attlist.get(attlist.size() - 1)
                .getAttentiontime().getTime());
        if (attlist.size() < 20) {
            more = false;
        }
        result.setContent(list, more, order, flag);
        return result;
    }

    public DoctorAccountDTO getDoctorInfo(String doctorId) {
        try {
            Map<String, Object> resultMap = docinfoService.findDoctorInfoByUid(doctorId);
            return new DoctorAccountDTO(resultMap);
        } catch (Exception e) {
            System.out.println("getDoctorInfo is null! " + doctorId);
            return null;
        }
    }
}
