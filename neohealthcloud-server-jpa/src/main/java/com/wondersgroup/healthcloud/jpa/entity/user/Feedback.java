package com.wondersgroup.healthcloud.jpa.entity.user;


import com.wondersgroup.healthcloud.jpa.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * app_tb_feedback(意见反馈)
 * <table border="1" >
 * <tr>
 * <th>设计名</th> <th>中文名</th> <th>说明</th>
 * </tr>
 * <tr><td>id</td><td>主键id</td><td>uuid</td></tr>
 * <tr><td>registerid</td><td>注册id</td><td> </td></tr>
 * <tr><td>suggesttime</td><td>提出时间</td><td> </td></tr>
 * <tr><td>comments</td><td>意见内容</td><td></td></tr>
 * <tr><td>email</td><td>邮箱</td><td> </td></tr>
 * <tr><td>qq</td><td>qq</td><td> </td></tr>
 * <tr><td>phone</td><td>电话</td><td></td></tr>
 * <tr><td>sign_read</td><td>阅读标志</td><td> </td></tr>
 * <tr><td>del_flag</td><td>删除标记</td><td>0：正常 1：删除</td></tr>
 * <tr><td>source_id</td><td>来源代码</td><td> </td></tr>
 * <tr><td>create_by</td><td>新建者id</td><td> </td></tr>
 * <tr><td>create_date</td><td>新建时间</td><td>0：正常 1：删除</td></tr>
 * <tr><td>update_by</td><td>修改者id</td><td> </td></tr>
 * <tr><td>update_date</td><td>修改时间</td><td> </td></tr>
 * </table>
 *
 * @author
 * @since 2.0
 */
@Data
@Entity
@Table(name = "app_tb_feedback")
public class Feedback extends BaseEntity {

    private static final long serialVersionUID = -3749000276600335705L;

    private String registerid;
    private String suggesttime;
    private String comments;
    private String contact;
    private String area;
    private String type;
}
