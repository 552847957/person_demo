package com.wondersgroup.healthcloud.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.wondersgroup.healthcloud.api.http.dto.doctor.signedPerson.SignedPersonDTO;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.utils.AppUrlH5Utils;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.services.assessment.AssessmentService;
import com.wondersgroup.healthcloud.services.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GWService extends HttpBaseService {

	@Autowired
	private UserService userService;

	@Autowired
	private AssessmentService assessmentService;

	@Autowired
	private AppUrlH5Utils appUrlH5Utils;

	Logger logger = LoggerFactory.getLogger(GWService.class);

	/**
	 * 签约居民列表
	 * @param famId
	 * @param name
	 * @param flag
	 * @return
	 */
	public JsonListResponseEntity<SignedPersonDTO> getPersonlist(String famId, String name, String flag) {
		JsonListResponseEntity<SignedPersonDTO> responseEntity = new JsonListResponseEntity<>();
		String url = SIGNED_CONNECTION_URL + "/api/sign/people?famId={famId}&name={name}&flag={flag}";
		JsonNode rootNode = restTemplate.getForObject(url, JsonNode.class, famId, encode(name), flag);

		int code = rootNode.get("code").asInt();
		JsonNode msgNode = rootNode.get("msg");
		String msg = msgNode == null ? null : msgNode.asText();

		JsonNode dataNode = rootNode.get("data");
		if (dataNode != null) {
			JsonNode moreNode = dataNode.get("more");
			JsonNode moreParamsNode = dataNode.get("more_params");
			JsonNode contentNode = dataNode.get("content");

			boolean more = moreNode == null ? null : moreNode.asBoolean();
			String moreParams = moreParamsNode == null ? null : moreParamsNode.get("flag").asText();
			List<SignedPersonDTO> personList = null;
			if (contentNode != null) {
				personList = jsonMapper.fromJson(contentNode.toString(),
						jsonMapper.contructCollectionType(List.class, SignedPersonDTO.class));

				for(SignedPersonDTO signedPersonDTO : personList){//todo
					Boolean isJky = false;
					Boolean isRisk = false;
					signedPersonDTO.setAvatar("");
					signedPersonDTO.setUid("");
					List<RegisterInfo> registerInfos = userService.findRegisterInfoByIdcard(signedPersonDTO.getPersoncard());
					if(registerInfos.size()>0){
						isJky = true;
						signedPersonDTO.setAvatar(registerInfos.get(0).getHeadphoto());
						signedPersonDTO.setUid(registerInfos.get(0).getRegisterid());
						//判断是否是 "危" 朱春柳的接口
						signedPersonDTO.setIsRisk(assessmentService.hasDiseases(registerInfos.get(0).getRegisterid()));
					}

					signedPersonDTO.setIsJky(isJky);
					signedPersonDTO.setIsRisk(isRisk);
					signedPersonDTO.setIsApo(false);
					signedPersonDTO.setIsDiabetes(false);
					signedPersonDTO.setIsHyp(false);

					signedPersonDTO.setHealthRecordsUrl(appUrlH5Utils.buildHealthRecord(signedPersonDTO.getPersoncard()));

				}
			}
			responseEntity.setContent(personList, more, null, moreParams);

		}
		responseEntity.setCode(code);
		responseEntity.setMsg(msg);
		return responseEntity;
	}

}
