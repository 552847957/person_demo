package doctor;

import com.wondersgroup.healthcloud.api.configurations.Application;
import com.wondersgroup.healthcloud.jpa.entity.diabetes.DoctorTubeSignUser;
import com.wondersgroup.healthcloud.jpa.repository.diabetes.DoctorTubeSignUserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by limenghua on 2017/6/6.
 *
 * @author limenghua
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("de")
public class DoctorTubeSignUserTest {
    private static final Logger logger = LoggerFactory.getLogger(DoctorTubeSignUserTest.class);

    @Autowired
    private DoctorTubeSignUserRepository doctorTubeSignUserRepository;

    @Test
    public void getAllTest() {
        List<DoctorTubeSignUser> doctorTubeSignUserList = doctorTubeSignUserRepository.getAll();
        logger.info(String.format("共查询到[%d条]数据", doctorTubeSignUserList.size()));
        for (DoctorTubeSignUser doctorTubeSignUser : doctorTubeSignUserList) {
            logger.info(doctorTubeSignUser.toString());
        }

        doctorTubeSignUserRepository.findAll();
    }
}
