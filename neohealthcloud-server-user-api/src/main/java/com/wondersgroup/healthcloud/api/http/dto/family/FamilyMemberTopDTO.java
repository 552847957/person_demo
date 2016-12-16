package com.wondersgroup.healthcloud.api.http.dto.family;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.api.http.dto.measure.SimpleMeasure;
import com.wondersgroup.healthcloud.services.user.dto.member.FamilyMemberInvitationAPIEntity;

import lombok.Data;

/**
 * 家庭首页DTO
 * Created by sunhaidi on 2016年12月7日
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FamilyMemberTopDTO {
    private FamilyMemberInvitationAPIEntity invitsation;
    private List<SimpleMeasure> measures;
}
