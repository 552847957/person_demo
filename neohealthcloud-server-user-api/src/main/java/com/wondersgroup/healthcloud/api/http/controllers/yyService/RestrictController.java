package com.wondersgroup.healthcloud.api.http.controllers.yyService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Maps;
import com.wondersgroup.healthcloud.api.http.dto.medical.RestrictUploadDto;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.exceptions.CommonException;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.repository.user.RegisterInfoRepository;
import com.wondersgroup.healthcloud.services.user.UserService;
import com.wondersgroup.healthcloud.services.yyService.VisitUserService;
import com.wondersgroup.healthcloud.utils.IdcardUtils;

@RestController
@RequestMapping("/api/user")
public class RestrictController {

	@Autowired
	private UserService userService;

	@Autowired
	private RegisterInfoRepository registerRepo;

	@Autowired
	private VisitUserService visitUserService;

	@Autowired
	private Environment env;

	/**
	 * 签约检查身份证是否有效
	 * 
	 * @param zjhm
	 * @return JsonResponseEntity<Object>
	 */
	@RequestMapping(value = "/restrictBind", method = RequestMethod.GET)
	@VersionRange
	public JsonResponseEntity<Object> restrictCheck(@RequestParam String zjhm, @RequestParam String registerId,
			@RequestParam(defaultValue = "0", required = false) String isBind) {
		JsonResponseEntity<Object> entity = new JsonResponseEntity<>();
		checkAgeIsCanBind(zjhm);
		checkIsBinded(registerId);

		if (userIsExist(zjhm)) {
			if (isBind.equals("1")) {
				// 用户已存在，不需要去注册了 直接绑定
				int result = registerRepo.updateByRegister(zjhm, registerId);
				if (result > 0) {
					entity.setMsg("签约成功！");
				} else {
					entity.setCode(2001);
					entity.setMsg("绑定失败！");
				}
			} else {
				entity.setMsg("验证通过,账号已签约");
			}
		} else {
			entity.setCode(4000);
			entity.setMsg("验证通过,请完善你的信息!");
		}
		return entity;
	}

	/**
	 * 签约
	 * 
	 * @param request
	 * @return JsonResponseEntity<Object>
	 */
	@RequestMapping(value = "/restrict", method = RequestMethod.POST)
	@VersionRange
	public JsonResponseEntity<Object> restrict(@RequestBody String request) {
		String url = getURL() + "rest/users/clientRegister.action";
		JsonResponseEntity<Object> entity = new JsonResponseEntity<Object>();
		JsonKeyReader reader = new JsonKeyReader(request);
		String password = "123456";
		String source = "05";
		String registerId = reader.readString("registerId", false);
		String zjhm = reader.readString("zjhm", false);
		String phone = reader.readString("phone", false);
		String xm = reader.readString("xm", false);
		String qx = reader.readString("qx", true);
		String qxcode = reader.readString("qxcode", false);
		String jd = reader.readString("jd", false);
		String jdcode = reader.readString("jdcode", false);
		String jw = reader.readString("jw", false);
		String jwcode = reader.readString("jwcode", false);
		String mph = reader.readString("mph", false);// 详细地址

		checkAgeIsCanBind(zjhm);
		checkIsBinded(registerId);

		if (userIsExist(zjhm)) {
			// 用户已存在，不需要去注册了 直接绑定
			int result = registerRepo.updateByRegister(zjhm, registerId);
			if (result > 0) {
				entity.setMsg("签约成功！");
			} else {
				entity.setCode(1001);
				entity.setMsg("修改 bind_personcard 失败！");
			}
			return entity;
		}
		String[] parm = { "password", password, "zjhm", zjhm, "source", source, "phone", phone, "xm", xm, "qx", qx,
				"qxcode", qxcode, "jd", jd, "jdcode", jdcode, "jw", jw, "jwcode", jwcode, "mph", mph };
		JsonNode node = visitUserService.postRequest(url, parm);
		if (1 == node.get("status").asInt()) {
			int result = registerRepo.updateByRegister(zjhm, registerId);
			if (result > 0) {
				entity.setMsg("签约成功！");
			} else {
				entity.setCode(1001);
				entity.setMsg("修改 bind_personcard 失败！");
			}
		} else {
			entity.setCode(1002);
			entity.setMsg(node.get("message").asText());
			return entity;
		}
		return entity;
	}

	/**
	 * 上传图片
	 * 
	 * @param response
	 * @param userId
	 * @param file
	 * @return JsonResponseEntity<Map<String,String>>
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	@VersionRange
	public JsonResponseEntity<Map<String, String>> upload(
			HttpServletRequest request,
			@RequestBody RestrictUploadDto uploadDto) throws IOException {
		JsonResponseEntity<Map<String, String>> responseEntity = new JsonResponseEntity<>();

		String url = getURL() + "rest/order/phoneAction!getIOSPhotos.action";

		String response = uploadDto.getResponse();
		String userId = uploadDto.getUserId();
		List<String> filePaths = uploadDto.getFile();

		HttpHeaders headers = buildHttpHeaders(userId);
		for (String string : filePaths) {
			MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
			requestBody.add("fileType", 03);
			requestBody.add("orderid", response);
			String filePath = request.getSession().getServletContext().getRealPath("/") + IdGen.uuid();
			download(string, filePath);
			File f = new File(filePath);
			requestBody.add("file", new FileSystemResource(f));
			if (f.exists()) {
				f.delete();
			}
			
			try {
				response = upload(url, new HttpEntity<MultiValueMap<String, Object>>(requestBody, headers));
			} catch (RuntimeException e) {
				responseEntity.setMsg(e.getMessage());
			}
			
		}

		Map<String, String> resultMap = ImmutableBiMap.of("response", response);
		responseEntity.setData(resultMap);
		responseEntity.setMsg("上传成功");
		return responseEntity;
	}

	/**
	 * 查询机构站点信息
	 * 
	 * @return JsonResponseEntity<Map<String,String>>
	 */
	@RequestMapping(value = "/findStation", method = RequestMethod.POST)
	@VersionRange
	public JsonResponseEntity<Map<String, String>> findStation(@RequestBody String request) {
		JsonKeyReader reader = new JsonKeyReader(request);
		String userId = reader.readString("userId", false);
		String fwid = reader.readString("fwid", false);
		String addressid = reader.readString("addressid", false);

		String url = getURL() + "rest/order/orderApplyInfoAction!findJgJwByFwidAndAddid.action";
		JsonResponseEntity<Map<String, String>> entity = new JsonResponseEntity<Map<String, String>>();
		String[] query = { "fwid", fwid, "addressid", addressid };
		JsonNode node = visitUserService.postRequest(userId, url, query);

		Map<String, String> resultMap = Maps.newHashMap();
		if (1 == node.get("status").asInt()) {
			JsonNode nod = node.get("response");
			if (nod == null || nod.get("id") == null) {
				entity.setCode(1000);
				entity.setMsg("未查询到站点信息！");
				return entity;
			}
			resultMap.put("id", getNodeValue(nod, "id"));
			resultMap.put("jwcode", getNodeValue(nod, "jwcode"));
			resultMap.put("Jw", getNodeValue(nod, "Jw"));
			resultMap.put("jgid", getNodeValue(nod, "jgid"));
			resultMap.put("jgname", getNodeValue(nod, "jgname"));
			resultMap.put("subid", getNodeValue(nod, "subid"));
			resultMap.put("subname", getNodeValue(nod, "subname"));
			resultMap.put("yxzt", getNodeValue(nod, "yxzt"));
			resultMap.put("hospital", getNodeValue(nod, "hospital"));
			resultMap.put("jc", getNodeValue(nod, "jc"));
			resultMap.put("lxr", getNodeValue(nod, "lxr"));
			resultMap.put("lxdh", getNodeValue(nod, "lxdh"));
			resultMap.put("address", getNodeValue(nod, "address"));
			resultMap.put("hostpitalid", getNodeValue(nod, "hostpitalid"));
			resultMap.put("subaddress", getNodeValue(nod, "subaddress"));
			entity.setData(resultMap);
			entity.setMsg("查询成功");
		} else {
			entity.setCode(1000);
			entity.setMsg(node.get("message").asText());
		}
		return entity;
	}

	public String getNodeValue(JsonNode node, String key) {
		if (node.get(key) == null) {
			return "";
		}
		return node.get(key).asText();
	}

	public boolean userIsExist(String personNo) {
		String url = getURL() + "/rest/users/toElmuPPye.action?idCard={idCard}";

		RestTemplate restTemplate = new RestTemplate();
		JsonNode resultNode = restTemplate.getForObject(url, JsonNode.class, personNo);
		JsonNode result = resultNode.get("response");
		boolean res = false;
		if (result != null && result.get("userId") != null && !StringUtils.isEmpty(result.get("userId").asText())) {
			res = true;
		}
		return res;
	}

	private void checkIsBinded(String userId) {
		RegisterInfo person = userService.getOneNotNull(userId);
		if (StringUtils.isNotEmpty(person.getBindPersoncard())) {
			throw new CommonException(2023, "已经绑定过服务！");
		}
	}

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	public void checkAgeIsCanBind(String personNo) {

		if (!IdcardUtils.validateCard(personNo)) {
			throw new CommonException(2022, "身份证号码有误");
		}
		int cardLen = personNo.length();
		String birthdayStr = "";
		if (cardLen == 18) {
			birthdayStr = personNo.substring(6, 10) + "-" + personNo.substring(10, 12) + "-"
					+ personNo.substring(12, 14);
		} else if (cardLen == 16) {
			birthdayStr = "19" + personNo.substring(6, 8) + "-" + personNo.substring(8, 10) + "-"
					+ personNo.substring(10, 12);
		}
		Date birthday = null;
		try {
			birthday = sdf.parse(birthdayStr);
		} catch (ParseException e) {
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(birthday);
		Date nowDate = new Date();
		calendar.add(Calendar.YEAR, 60);
		Boolean isFalse = calendar.getTime().after(nowDate);
		if (isFalse) {
			throw new CommonException(2022, "年龄未到60岁！");
		}
	}

	private String getURL() {
		return this.env.getProperty("yyservice.service.host") + "/";
	}

	private void download(String urlString, String filename) throws IOException {
		URL url = new URL(urlString);
		URLConnection con = url.openConnection();
		InputStream is = con.getInputStream();
		byte[] bs = new byte[1024];
		int len;
		OutputStream os = new FileOutputStream(filename);
		while ((len = is.read(bs)) != -1) {
			os.write(bs, 0, len);
		}
		os.close();
		is.close();
	}

	private String upload(String url, HttpEntity<MultiValueMap<String, Object>> httpEntity) {
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode node = null;
		try {
			node = objectMapper.readValue(responseEntity.getBody(), JsonNode.class);
		} catch (Exception e) {
			throw new RuntimeException("服务异常");
		}

		if (1 == node.get("status").asInt()) {
			return node.get("response").asText();
		} else {
			throw new RuntimeException(node.get("message").asText());
		}
	}

	private HttpHeaders buildHttpHeaders(String userId) {
		String[] header = visitUserService.getRequestHeaderByUid(userId, true);
		HttpHeaders headers = new HttpHeaders();
		for (int i = 0; i < header.length; i += 2) {
			headers.add(header[i], header[i + 1]);
		}
		return headers;
	}

}
