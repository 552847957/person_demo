package com.wondersgroup.healthcloud.api.http.controllers.doctor.gw;

import com.wondersgroup.healthcloud.api.http.dto.doctor.signedPerson.SignedPersonDTO;
import com.wondersgroup.healthcloud.api.service.GWService;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 签约
 * 
 * @author tanxueliang
 *
 */
@RestController
@RequestMapping("/api/signed")
public class SignedController {

	@Autowired
	GWService gwService;

	@Autowired
	GwRestClient restClient;

	/**
	 * 居民列表
	 * 
	 * @param
	 * @param name
	 * @param flag
	 * @return
	 */
	@RequestMapping(value = "/personlist", method = RequestMethod.GET)
	@VersionRange
	public JsonListResponseEntity<SignedPersonDTO> personlist(@RequestParam String famId,
			@RequestParam(defaultValue = "", required = false) String name,
			@RequestParam(defaultValue = "", required = false) String flag) {
		return gwService.getPersonlist(famId, name, flag);
	}
}
