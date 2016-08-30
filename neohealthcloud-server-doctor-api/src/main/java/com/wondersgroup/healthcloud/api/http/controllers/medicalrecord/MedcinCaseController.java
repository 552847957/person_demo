package com.wondersgroup.healthcloud.api.http.controllers.medicalrecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;
import com.qiniu.util.StringUtils;
import com.wondersgroup.healthcloud.api.http.dto.doctor.medicalrecord.AddMedcinCaseForm;
import com.wondersgroup.healthcloud.api.http.dto.doctor.medicalrecord.AddMedicalRecordForm;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.helper.medicincase.MedicinCaseConstant;
import com.wondersgroup.healthcloud.jpa.entity.medicalrecord.MedicalCase;
import com.wondersgroup.healthcloud.jpa.entity.medicalrecord.MedicalRecord;
import com.wondersgroup.healthcloud.services.medicalrecord.ManageMedicalCaseService;
import com.wondersgroup.healthcloud.utils.DateFormatter;
import com.wondersgroup.healthcloud.utils.ImageUtils;

/**
 * Created by qiujun on 2015/9/5.
 */
@RestController
public class MedcinCaseController {

	private static final Logger log = LoggerFactory.getLogger("SRTE");
	private static final int PAGE_SIZE = 20;

	@Resource
	private ManageMedicalCaseService manageMedicalCaseService;

	@RequestMapping(value = "/api/getMedicialCases", method = RequestMethod.GET)
	@VersionRange
	public JsonListResponseEntity<MedicalCase> getDoctorMedicialCase(@RequestParam String uid,
			@RequestParam(required = false) Integer flag) {
		JsonListResponseEntity<MedicalCase> entity = new JsonListResponseEntity<>();
		try {
			Integer position = 0;

			if (flag != null) {
				position = flag;
			}
			List<MedicalCase> cases = manageMedicalCaseService.getMedicalCaseByDoctorId(uid);
			if (cases != null && !cases.isEmpty()) {
				entity = new Page().handleResponseEntity(entity, position, (position + PAGE_SIZE), PAGE_SIZE, cases);
			} else {
				entity.setContent((List) Collections.emptyList());
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			entity.setContent((List) Collections.emptyList());
			entity.setCode(3009);
			entity.setMsg("调用失败");
		}

		return entity;
	}

	/**
	 * 根据用户名搜索病历
	 *
	 * @param patientname
	 * @param flag
	 * @return
	 */
	@RequestMapping(value = "/api/searchCase", method = RequestMethod.GET)
	@VersionRange
	public JsonListResponseEntity<MedicalCase> searchMedicalCaseByName(
			@RequestParam(defaultValue = "") String patientname, @RequestParam String doctorId,
			@RequestParam(required = false) Integer flag) {
		JsonListResponseEntity<MedicalCase> entity = new JsonListResponseEntity<>();
		try {
			Integer position = 0;
			if (flag != null) {
				position = flag;
			}
			List<MedicalCase> cases = manageMedicalCaseService.searchMedicalCaseByName(patientname, doctorId);
			if (cases != null && !cases.isEmpty()) {
				entity = new Page().handleResponseEntity(entity, position, (position + PAGE_SIZE), PAGE_SIZE, cases);
			} else {
				entity.setContent((List) Collections.emptyList());
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			entity.setContent((List) Collections.emptyList());
			entity.setCode(3009);
			entity.setMsg("调用失败");
		}
		return entity;
	}

	/**
	 * 新建一个病历
	 * 
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/api/addMedcinCase", method = RequestMethod.POST)
	@VersionRange
	public JsonResponseEntity<String> addMedcinCase(@RequestBody AddMedcinCaseForm form) throws IOException {

		JsonResponseEntity<String> body = new JsonResponseEntity<>();
		MedicalCase medicalCase = generateMedcinCase(new MedicalCase(), form);

		String actionType = form.getActionType();
		String remarks = form.getRemarks();
		String imgUrls = form.getImgUrls();

		MedicalRecord record = generateMedicalRecord(new MedicalRecord(), medicalCase, actionType, remarks, imgUrls);
		try {
			manageMedicalCaseService.addMedicalCase(medicalCase);
			manageMedicalCaseService.addNewRecord(record);
			body.setMsg("添加病历成功");
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			body.setCode(3009);
			body.setMsg("调用失败");
		}
		return body;
	}

	private MedicalCase generateMedcinCase(MedicalCase medicalCase, AddMedcinCaseForm form) {

		medicalCase.setBirthDay(form.getBirthDay());
		Calendar nowDate = Calendar.getInstance();
		Calendar birth = Calendar.getInstance();
		nowDate.setTime(new Date());
		birth.setTime(DateFormatter.parseDate(medicalCase.getBirthDay()));
		medicalCase.setAge(getUserAge(nowDate, birth) + "岁");
		medicalCase.setCaseId(UUID.randomUUID().toString().replace("-", ""));
		medicalCase.setDoctorId(form.getDoctorId());
		medicalCase.setGendar(form.getGender());
		medicalCase.setPatientname(form.getPatientname());
		medicalCase.setLastUpdateDate(DateFormatter.dateFormat(new Date()));
		if (!StringUtils.isNullOrEmpty(form.getMobile())) {
			medicalCase.setMobile(form.getMobile());
		}
		if (!StringUtils.isNullOrEmpty(form.getMedcinCard())) {
			medicalCase.setMedicinCard(form.getMedcinCard());
		}
		return medicalCase;
	}

	@RequestMapping(value = "/api/updateBasicInfo", method = RequestMethod.POST)
	@VersionRange
	public JsonResponseEntity<String> updateBasicInfo(@RequestBody String request) {

		JsonResponseEntity<String> body = new JsonResponseEntity<String>();
		JsonKeyReader reader = new JsonKeyReader(request);
		String caseId = reader.readString("caseId", true);
		MedicalCase medicalCase = manageMedicalCaseService.getMedicalCaseByCaseId(caseId);
		try {
			if (medicalCase != null) {
				if (!StringUtils.isNullOrEmpty(reader.readString("patientname", true)))
					medicalCase.setPatientname(reader.readString("patientname", true));

				if (!StringUtils.isNullOrEmpty(reader.readString("gendar", true)))
					medicalCase.setGendar(reader.readString("gendar", true));

				if (!StringUtils.isNullOrEmpty(reader.readString("medicinCard", true)))
					medicalCase.setMedicinCard(reader.readString("medicinCard", true));
				else
					medicalCase.setMedicinCard("");

				if (!StringUtils.isNullOrEmpty(reader.readString("birthDay", true))) {
					medicalCase.setBirthDay(reader.readString("birthDay", true));
					Calendar nowDate = Calendar.getInstance();
					Calendar birth = Calendar.getInstance();
					nowDate.setTime(new Date());
					birth.setTime(DateFormatter.parseDate(medicalCase.getBirthDay()));
					medicalCase.setAge(getUserAge(nowDate, birth) + "岁");
				}

				if (!StringUtils.isNullOrEmpty(reader.readString("mobile", true)))
					medicalCase.setMobile(reader.readString("mobile", true));
				else
					medicalCase.setMobile("");

				medicalCase.setLastUpdateDate(DateFormatter.dateFormat(new Date()));

				manageMedicalCaseService.updateMedicalCaseBasicInfo(medicalCase);
				MedicalRecord record = new MedicalRecord();
				record.setActionType(MedicinCaseConstant.UPDATE_CASE_BASIC_INFO_TYPE);
				record.setActionName(MedicinCaseConstant.UPDATE_CASE_BASIC_INFO);
				record.setRecordId(UUID.randomUUID().toString().replace("-", ""));
				record.setCaseId(medicalCase.getCaseId());
				record.setAddDate(new Date());
				manageMedicalCaseService.addNewRecord(record);
				body.setMsg("修改信息成功");
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			body.setMsg("修改信息失败");
			body.setCode(3010);
		}

		return body;
	}

	private String getUserAge(Calendar nowDate, Calendar birthDay) {
		int year = nowDate.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
		return String.valueOf(year);
	}

	@RequestMapping(value = "/api/getSingleMedicalCase", method = RequestMethod.GET)
	@VersionRange
	public JsonResponseEntity<MedicalCase> getSingleMedicalCase(@RequestParam String caseId) {

		JsonResponseEntity<MedicalCase> body = new JsonResponseEntity();
		try {
			MedicalCase medicalCase = manageMedicalCaseService.getMedicalCaseByCaseId(caseId);
			body.setData(medicalCase);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			body.setMsg("调用失败");
			body.setCode(3011);
		}
		return body;
	}

	@RequestMapping(value = "/api/getSingleMedicalRecord", method = RequestMethod.GET)
	@VersionRange
	public JsonResponseEntity<MedicalRecord> getSingleMedicalRecord(@RequestParam String recordId) {

		JsonResponseEntity<MedicalRecord> body = new JsonResponseEntity();
		try {
			MedicalRecord record = manageMedicalCaseService.getSingleMedicalRecordByRecordId(recordId);
			body.setData(record);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			body.setMsg("调用失败");
			body.setCode(3011);
		}
		return body;
	}

	@RequestMapping(value = "/api/getMedicalRecord", method = RequestMethod.GET)
	@VersionRange
	public JsonListResponseEntity<MedicalRecord> getMedicalRecord(@RequestHeader(value = "screen-width") String width,
			@RequestParam String caseId, @RequestParam(required = false) Integer flag) {

		JsonListResponseEntity<MedicalRecord> body = new JsonListResponseEntity();
		Integer position = 0;
		if (flag != null) {
			position = flag;
		}
		try {
			List<MedicalRecord> records = manageMedicalCaseService.getMedicalRecordByCaseId(caseId);
			List<MedcinRecordCp> cp = convertCps(records, width);
			if (records != null && !records.isEmpty()) {
				body = new Page().handleResponseEntity(body, position, (position + PAGE_SIZE), PAGE_SIZE, cp);
			} else {
				body.setContent((List) Collections.emptyList());
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			body.setCode(3012);
			body.setMsg("调用失败");
		}

		return body;
	}

	@RequestMapping(value = "/api/addNewMedicalRecord", method = RequestMethod.POST)
	@VersionRange
	public JsonResponseEntity<String> addNewMedicalRecord(@RequestBody AddMedicalRecordForm form) {

		JsonResponseEntity<String> body = new JsonResponseEntity<>();
		try {
			MedicalCase medicalCase = manageMedicalCaseService.getMedicalCaseByCaseId(form.getCaseId());
			medicalCase.setLastUpdateDate(DateFormatter.dateTimeFormat(new Date()));

			MedicalRecord record = generateMedicalRecord(new MedicalRecord(), medicalCase, form.getActionType(),
					form.getRemarks(), form.getImgUrls());
			manageMedicalCaseService.addNewRecord(record);
			manageMedicalCaseService.updateMedicalCaseBasicInfo(medicalCase);
			body.setMsg("新增病历记录成功");
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			body.setCode(3012);
			body.setMsg("新增病历记录失败");
		}
		return body;
	}

	private MedicalRecord generateMedicalRecord(MedicalRecord record, MedicalCase medicalCase, String actionType,
			String remarks, String imgUrls) {

		record.setRecordId(UUID.randomUUID().toString().replace("-", ""));
		record.setCaseId(medicalCase.getCaseId());
		record.setActionType(actionType);
		record.setActionName(MedicinCaseConstant.getActionDesc(actionType));
		if (!StringUtils.isNullOrEmpty(imgUrls)) {
			record.setImgs(imgUrls);
		}
		record.setAddDate(new Date());
		if (!StringUtils.isNullOrEmpty(remarks)) {
			record.setRemark(remarks);
		}

		return record;
	}

	private List<MedcinRecordCp> convertCps(List<MedicalRecord> records, String width) {

		List<MedcinRecordCp> cps = Lists.newArrayList();
		if (records != null && !records.isEmpty()) {
			for (MedicalRecord record : records) {
				MedcinRecordCp cp = new MedcinRecordCp(record, width);
				cps.add(cp);
			}
		}
		return cps;
	}

	/**
	 * 分页
	 *
	 * @param
	 */
	private class Page {

		public boolean more;

		public List pageObject(List origin, int fromIndex, int toIndex) {
			if (origin == null) {
				return null;
			}
			if (toIndex >= origin.size())
				toIndex = origin.size();

			return new ArrayList<>(origin.subList(fromIndex, toIndex));
		}

		public JsonListResponseEntity handleResponseEntity(JsonListResponseEntity body, int fromIndex, int toIndex,
				int pageSize, List cases) {
			if (body == null)
				return null;
			List temp = pageObject(cases, fromIndex, toIndex);
			if (cases.size() > pageSize && fromIndex < cases.size() && temp.size() == PAGE_SIZE) {
				body.setContent(temp, true, "updateTime", String.valueOf(fromIndex + temp.size()));
			} else {
				body.setContent(temp, false, "updateTime", String.valueOf(cases.size()));
			}
			return body;
		}

		public List getRecordByPaging(int fromIndex, int toIndex, int pageSize, List cases) {
			if (cases == null)
				return null;
			List temp = pageObject(cases, fromIndex, toIndex);
			more = cases.size() > pageSize && fromIndex < cases.size() && temp.size() == PAGE_SIZE;
			return temp;
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	class MedcinRecordCp {

		public String getActionName() {
			return actionName;
		}

		public void setActionName(String actionName) {
			this.actionName = actionName;
		}

		public String getActionType() {
			return actionType;
		}

		public void setActionType(String actionType) {
			this.actionType = actionType;
		}

		public String getAddDate() {
			return addDate;
		}

		public void setAddDate(String addDate) {
			this.addDate = addDate;
		}

		public String getCaseId() {
			return caseId;
		}

		public void setCaseId(String caseId) {
			this.caseId = caseId;
		}

		public String getImgs() {
			return imgs;
		}

		public void setImgs(String imgs) {
			this.imgs = imgs;
		}

		public String getRecordId() {
			return recordId;
		}

		public void setRecordId(String recordId) {
			this.recordId = recordId;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

		public String getThumbs() {
			return thumbs;
		}

		public void setThumbs(String thumbs) {
			this.thumbs = thumbs;
		}

		public MedcinRecordCp(MedicalRecord record, String width) {
			this.actionName = record.getActionName();
			this.actionType = record.getActionType();
			this.addDate = DateFormatter.dateTimeFormat(record.getAddDate());
			this.caseId = record.getCaseId();
			this.imgs = record.getImgs();
			this.recordId = record.getRecordId();
			this.remark = record.getRemark();
			this.thumbs = handleThumbs(record.getImgs(), width);
		}

		private String recordId;
		private String caseId;
		private String actionName;
		private String actionType;
		private String remark;
		private String imgs;
		private String addDate;
		private String thumbs;

		private String handleThumbs(String imgs, String width) {

			if (!StringUtils.isNullOrEmpty(imgs)) {
				String thumbs;
				String[] imgsArray = imgs.split(",");
				if (imgs.length() == 1) {
					ImageUtils.Image image = ImageUtils.getImage(imgsArray[0]);
					thumbs = ImageUtils.getBigThumb(image, width);
				} else {
					StringBuffer sb = new StringBuffer();
					for (String imgUrl : imgsArray) {
						sb.append(ImageUtils.getSquareThumb(imgUrl, width)).append(",");
					}
					thumbs = sb.toString();
					thumbs = thumbs.substring(0, thumbs.lastIndexOf(","));
				}
				return thumbs;
			}
			return null;
		}
	}

}
