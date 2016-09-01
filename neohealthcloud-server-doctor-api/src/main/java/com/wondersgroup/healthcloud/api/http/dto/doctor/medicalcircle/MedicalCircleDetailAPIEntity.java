package com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

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

    public MedicalCircleDetailAPIEntity(){
        
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
