package doctor;

import com.wondersgroup.healthcloud.api.configurations.Application;
import com.wondersgroup.healthcloud.jpa.constant.CommonConstant;
import com.wondersgroup.healthcloud.jpa.entity.diabetes.DoctorTubeSignUser;
import com.wondersgroup.healthcloud.jpa.repository.diabetes.DoctorTubeSignUserRepository;
import com.wondersgroup.healthcloud.services.group.PatientGroupService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by limenghua on 2017/6/8.
 *
 * @author limenghua
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("de")
public class PatientGroupServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(PatientGroupServiceTest.class);

    @Autowired
    private DoctorTubeSignUserRepository doctorTubeSignUserRepository;
    @Autowired
    private PatientGroupService patientGroupService;

    @Test
    public void getUserIdsByGroupIdTest() {
        Integer groupId = 2;
        List<String> list = patientGroupService.getUserIdsByGroupId(groupId);
        logger.info(String.format("组[%d]内包含用户[%s]", groupId, list));
        Page<DoctorTubeSignUser> pageData = doctorTubeSignUserRepository.queryByDelFlagAndIdIn(CommonConstant.USED_DEL_FLAG, list, new PageRequest(0, 2));
        for (DoctorTubeSignUser user : pageData.getContent()) {
            logger.info(user.toString());
        }
    }
}
