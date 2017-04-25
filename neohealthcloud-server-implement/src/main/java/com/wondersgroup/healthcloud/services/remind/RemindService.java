package com.wondersgroup.healthcloud.services.remind;

import com.wondersgroup.healthcloud.jpa.entity.remind.Remind;
import com.wondersgroup.healthcloud.jpa.entity.remind.RemindItem;
import com.wondersgroup.healthcloud.jpa.entity.remind.RemindTime;
import com.wondersgroup.healthcloud.services.remind.dto.RemindDTO;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Admin on 2017/4/11.
 */
public interface RemindService {
    List<RemindDTO> list(String userId, int pageNo, int pageSize);
    RemindDTO detail(String id);
    @Transactional
    int saveAndUpdate(Remind remind, RemindItem[] remindItems, RemindTime[] remindTimes, RemindItem[] delRemindItems, RemindTime[] delRemindTimes);
    @Transactional
    int enableOrDisableRemind(String id);
    @Transactional
    int deleteRemind(String id);
    int medicationReminder(String id, String remindTimeId, String internalUrl);
}
