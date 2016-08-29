package com.wondersgroup.healthcloud.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.wondersgroup.healthcloud.api.http.dto.doctor.signedPerson.SignedPersonDTO;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GWService extends HttpBaseService {

	Logger logger = LoggerFactory.getLogger("EX");

	/**
	 * 居民列表
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
					signedPersonDTO.setAvatar("");

					signedPersonDTO.setIsRisk(false);
					signedPersonDTO.setIsJky(false);
					signedPersonDTO.setIsApo(false);

					signedPersonDTO.setIsDiabetes(false);
					signedPersonDTO.setIsHyp(false);

				}
			}
			responseEntity.setContent(personList, more, null, moreParams);

		}
		responseEntity.setCode(code);
		responseEntity.setMsg(msg);
		return responseEntity;
	}

}
