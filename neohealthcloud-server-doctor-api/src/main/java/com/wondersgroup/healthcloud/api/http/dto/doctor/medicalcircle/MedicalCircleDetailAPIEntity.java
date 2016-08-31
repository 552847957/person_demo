package com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.dict.DictCache;
import com.wondersgroup.healthcloud.jpa.entity.circle.ArticleAttach;
import com.wondersgroup.healthcloud.jpa.entity.circle.ArticleTransmit;
import com.wondersgroup.healthcloud.jpa.entity.medicalcircle.MedicalCircle;
import com.wondersgroup.healthcloud.jpa.entity.medicalcircle.MedicalCircleCommunity;
import com.wondersgroup.healthcloud.jpa.entity.medicalcircle.MedicalCircleReply;
import com.wondersgroup.healthcloud.services.medicalcircle.MedicalCircleService;
import com.wondersgroup.healthcloud.utils.ImageUtils;
import com.wondersgroup.healthcloud.utils.TimeAgoUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MedicalCircleDetailAPIEntity {
    private String                 circle_id;      //帖子id
    private String                 doctor_id;      //医生id
    private String                 avatar;         //图像
    private String                 name;           //名称
    private String                 hospital;       //医院名称
    private String                 ago;            //时间
    private String                 tag;            //标签
    private String                 color;          //颜色
    private Long                   like_num;       //赞数量
    private Long                   comment_num;    //评论数
    private Boolean                is_liked;
    private Long                   views;         //浏览次数
    private Boolean                is_collected;  //是否收藏
    private String[]               liked_doc_name; //点赞人名
    private String                 like_doc_names; //点赞人名 逗号分隔
    private Integer                circle_type;   //圈子类型 1学术贴 2病例 3动态
    private NoteAPIEntity          note;          //帖子
    private CaseAPIEntity          cases;         //病例
    private DynamicAPIEntity       dynamic;       //动态
    private ShareAPIEntity         share;         //分享
    private List<CommentAPIEntity> comment_list;  //评论列表

    public MedicalCircleDetailAPIEntity(MedicalCircleDependence dep, MedicalCircle mc, String screen_width, String uid) {
        if (dep == null && mc == null) {
            return;
        }
        DictCache dictCache = dep.getDictCache();
        MedicalCircleService mcService = dep.getMcService();

        if (true) {
            String circle_id = mc.getId();
            //            String doctor_id = doctor.getRegisterid();
            //            this.setDoctor_id(doctor.getRegisterid());
            //            this.setAgo(TimeAgoUtils.ago(mc.getSendtime()));
            //            this.setAvatar(doctor.getHeadphoto());
            this.setCircle_id(circle_id);
            this.setCircle_type(mc.getType());
            this.setComment_num(mcService.getCommentsNum(circle_id));
            this.setLike_num(null != mc.getPraisenum() ? mc.getPraisenum() : 0);
            //            this.setHospital(doctor.getHospitalName());
            if (StringUtils.isNotEmpty(uid)) {
                //                this.setIs_liked(CircleLikeUtils.isLikeOne(circle_id, uid));
            }
            //            this.setName(doctor.getName());
            this.setTag(dictCache.queryTagName(mc.getTagid()));
            this.setColor(dictCache.queryTagColor(mc.getTagid()));
            //            this.setViews(mcService.getCircleViews(circle_id));//redis
            this.setIs_collected(mcService.checkCollect(circle_id, uid, 1));

            //            DoctorEntity[] doctors = docUtils.getDoctors(CircleLikeUtils.likeUserIds(circle_id));
            //            String[] docLikeNames = new String[doctors.length];
            //            if(doctors.length>0){
            //                for (int i = 0; i < doctors.length; i++) {
            //                    DoctorEntity doc = doctors[i];
            //                    if(doc!=null) {
            //                        String name = doc.getName();
            //                        if(StringUtils.isEmpty(name)){
            //                            name = doc.getNickname();
            //                        }
            //                        docLikeNames[i] = doc.getRegisterid() + ":" + name;
            //                        this.like_doc_names ="";
            //                        if (i == doctors.length - 1) {
            //                            this.like_doc_names += name;
            //                        } else {
            //                            this.like_doc_names += name + ",";
            //                        }
            //                    }
            //                }
            //                this.setLiked_doc_name(docLikeNames);
            //            }
            Integer type = mc.getType();
            this.setCircle_type(type);
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
            if (type == 1) {//帖子
                NoteAPIEntity note = new NoteAPIEntity();
                note.setContent(mc.getContent());
                note.setImages(imageAPIEntities);
                note.setTitle(mc.getTitle());
                this.setNote(note);
            } else if (type == 2) {//病例
                CaseAPIEntity cases = new CaseAPIEntity();
                cases.setTitle(mc.getTitle());
                cases.setContent(mc.getContent());
                cases.setImages(imageAPIEntities);
                this.setCases(cases);
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
                this.setDynamic(dynamic);
            }

            List<MedicalCircleCommunity> comments = mcService.getMedicalCircleComments(circle_id, "discusstime:asc",
                    new Date(0));
            List<CommentAPIEntity> commentlist = new ArrayList<CommentAPIEntity>();
            int cfloor = 1;
            for (MedicalCircleCommunity comment : comments) {
                CommentAPIEntity commentEntity = new CommentAPIEntity();
                //                DoctorEntity commentDoctor = docUtils.getDoctor(comment.getDoctorid());
                //                if(commentDoctor==null){
                //                    continue;
                //                }
                commentEntity.setAgo(TimeAgoUtils.ago(comment.getDiscusstime()));
                //                commentEntity.setAvatar(commentDoctor.getHeadphoto());
                commentEntity.setContent(comment.getContent());
                commentEntity.setFloor(mcService.getFloor(cfloor));
                commentEntity.setDoctor_id(comment.getDoctorid());
                //                commentEntity.setName(commentDoctor.getName());

                int rfloor = 1;
                List<CommentAPIEntity> replyEntitylist = new ArrayList<CommentAPIEntity>();
                List<MedicalCircleReply> commentReplyList = mcService.getCommentReplyList(comment.getId(), new Date(0),
                        "discusstime:asc", 5);
                for (MedicalCircleReply reply : commentReplyList) {
                    CommentAPIEntity replyEntity = new CommentAPIEntity();
                    //                    DoctorEntity replyDoctor = docUtils.getDoctor(comment.getDoctorid());
                    //                    if(replyDoctor==null){
                    //                        continue;
                    //                    }
                    //                    replyEntity.setName(replyDoctor.getName());
                    replyEntity.setDoctor_id(reply.getDoctorid());
                    replyEntity.setAgo(TimeAgoUtils.ago(reply.getDiscusstime()));
                    //                    replyEntity.setAvatar(replyDoctor.getHeadphoto());
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
            this.setComment_list(commentlist);

            ShareAPIEntity shareEntity = new ShareAPIEntity();
            String content = mc.getContent();
            if (StringUtils.isNotBlank(content) && content.length() > 100) {
                content = content.substring(0, 100) + "...";
            }
            shareEntity.setTitle(StringUtils.defaultString(mc.getTitle(), "万达全程健康云"));
            shareEntity.setDesc(content);
            shareEntity.setThumb("http://img.wdjky.com/app/ic_launcher");
            //            shareEntity.setUrl(AppDoctorUrlH5Utils.buildMedicalCircleTopicView(mc.getId()));
            this.share = shareEntity;
        }
    }

    public String getCircle_id() {
        return circle_id;
    }

    public void setCircle_id(String circle_id) {
        this.circle_id = circle_id;
    }

    public String getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(String doctor_id) {
        this.doctor_id = doctor_id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getAgo() {
        return ago;
    }

    public void setAgo(String ago) {
        this.ago = ago;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Long getLike_num() {
        return like_num;
    }

    public void setLike_num(Long like_num) {
        this.like_num = like_num;
    }

    public Long getComment_num() {
        return comment_num;
    }

    public void setComment_num(Long comment_num) {
        this.comment_num = comment_num;
    }

    public Boolean getIs_liked() {
        return is_liked;
    }

    public void setIs_liked(Boolean is_liked) {
        this.is_liked = is_liked;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public String[] getLiked_doc_name() {
        return liked_doc_name;
    }

    public void setLiked_doc_name(String[] liked_doc_name) {
        this.liked_doc_name = liked_doc_name;
    }

    public Integer getCircle_type() {
        return circle_type;
    }

    public void setCircle_type(Integer circle_type) {
        this.circle_type = circle_type;
    }

    public NoteAPIEntity getNote() {
        return note;
    }

    public void setNote(NoteAPIEntity note) {
        this.note = note;
    }

    public CaseAPIEntity getCases() {
        return cases;
    }

    public void setCases(CaseAPIEntity cases) {
        this.cases = cases;
    }

    public DynamicAPIEntity getDynamic() {
        return dynamic;
    }

    public void setDynamic(DynamicAPIEntity dynamic) {
        this.dynamic = dynamic;
    }

    public List<CommentAPIEntity> getComment_list() {
        return comment_list;
    }

    public void setComment_list(List<CommentAPIEntity> comment_list) {
        this.comment_list = comment_list;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLike_doc_names() {
        return like_doc_names;
    }

    public void setLike_doc_names(String like_doc_names) {
        this.like_doc_names = like_doc_names;
    }

    public Boolean getIs_collected() {
        return is_collected;
    }

    public void setIs_collected(Boolean is_collected) {
        this.is_collected = is_collected;
    }

    public ShareAPIEntity getShare() {
        return share;
    }

    public void setShare(ShareAPIEntity share) {
        this.share = share;
    }
}
