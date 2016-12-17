package com.wondersgroup.healthcloud.api.http.controllers.appointment;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.api.http.dto.appointment.ContactDetailDTO;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentContact;
import com.wondersgroup.healthcloud.services.appointment.AppointmentContactService;
import com.wondersgroup.healthcloud.services.appointment.exception.AddContactErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by longshasha on 16/12/8.
 * 有关就诊人的接口
 */
@RestController
@RequestMapping("/api/reservation")
public class AppointmentContactController {

    @Autowired
    private AppointmentContactService appointmentContactService;

    /**
     * 添加联系人(appointment_contact_tb表，根据id)
     *
     * @param request
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/contacts/add", method = RequestMethod.POST)
    public JsonResponseEntity<ContactDetailDTO> doSaveContact(
            @RequestBody String request) {
        JsonResponseEntity<ContactDetailDTO> body = new JsonResponseEntity<ContactDetailDTO>();

        JsonKeyReader reader = new JsonKeyReader(request);
        String uid = reader.readString("uid", false);
        String name = reader.readString("name", false);
        String idcard = reader.readString("idcard", false);
        String mobile = reader.readString("mobile", false);
        String mediCardId = reader.readString("medi_card_id", true);

        AppointmentContact contact = appointmentContactService.addAppointmentContact(uid,name,idcard,mobile,mediCardId);
        ContactDetailDTO contactDetailDTO = new ContactDetailDTO(contact);
        if (contactDetailDTO!=null) {
            body.setData(contactDetailDTO);
            body.setMsg("添加成功");
        } else {
            throw new AddContactErrorException();
        }

        return body;
    }

    /**
     * 根据uid查询就诊人列表
     *
     * @param uid
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/contacts/list", method = RequestMethod.GET)
    public JsonListResponseEntity<ContactDetailDTO> getAppointmentContactList(
            @RequestParam(value = "uid", required = true) String uid) {

        JsonListResponseEntity<ContactDetailDTO> response = new JsonListResponseEntity<>();

        List<ContactDetailDTO> newlists = Lists.newArrayList();

        List<AppointmentContact> list = appointmentContactService.getAppointmentContactList(uid);
        for (AppointmentContact appointmentContact : list) {
            if (null != appointmentContact) {
                ContactDetailDTO entity = new ContactDetailDTO(appointmentContact);
                newlists.add(entity);
            }
        }
        response.setCode(0);
        response.setContent(newlists);
        return response;
    }

    /**
     * 获取就诊人详情
     * @param id
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/contacts/detail", method = RequestMethod.GET)
    public JsonResponseEntity<ContactDetailDTO> getAppointmentContactById(
            @RequestParam(value = "id", required = true) String id) {

        JsonResponseEntity<ContactDetailDTO> response = new JsonResponseEntity<>();

        AppointmentContact appointmentContact = appointmentContactService.getAppointmentContactById(id);
        ContactDetailDTO entity = new ContactDetailDTO(appointmentContact);

        response.setData(entity);
        return response;
    }

    /**
     * 根据Uid获取默认就诊人
     * @param uid
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/contacts/default", method = RequestMethod.GET)
    public JsonResponseEntity<ContactDetailDTO> getDefaultAppointmentContact(
            @RequestParam(value = "uid", required = true) String uid) {

        JsonResponseEntity<ContactDetailDTO> response = new JsonResponseEntity<>();
        AppointmentContact appointmentContact = appointmentContactService.getDefaultAppointmentContactByUid(uid);
        ContactDetailDTO entity = new ContactDetailDTO(appointmentContact);
        response.setData(entity);
        return response;
    }

}
