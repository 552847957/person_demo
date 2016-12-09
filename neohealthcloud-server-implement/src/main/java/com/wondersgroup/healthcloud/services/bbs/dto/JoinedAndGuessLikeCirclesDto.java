package com.wondersgroup.healthcloud.services.bbs.dto;

import java.util.List;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.wondersgroup.healthcloud.services.bbs.dto.circle.CircleListDto;

/**
 * 
 * @author zhongshuqing 2016.12.08
 *
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JoinedAndGuessLikeCirclesDto {
    private List<CircleListDto> joinedList;
    private List<CircleListDto> guessLikeList;
}
