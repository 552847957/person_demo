package com.wondersgroup.healthcloud.api.http.dto.medical;

import java.util.List;

import lombok.Data;

@Data
public class RestrictUploadDto {

	private String userId;
	
	private String response;

	private List<String> file;

}
