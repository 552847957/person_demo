package com.wondersgroup.healthcloud.services.bbs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.wondersgroup.healthcloud.services.bbs.dto.circle.CircleBannerDto;
import com.wondersgroup.healthcloud.services.bbs.dto.circle.CircleListDto;
import lombok.Data;

import java.util.List;

/**
 * Created by limenghua on 2016/8/16.
 *
 * @author limenghua
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BannerAndMyCirclesDto {
    private List<CircleBannerDto> bannerList;
    private List<CircleListDto> myCircleList;
}
