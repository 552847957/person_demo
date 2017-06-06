package doctor;

import com.wondersgroup.healthcloud.api.configurations.Application;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorInfo;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorInfoRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by limenghua on 2017/6/6.
 *
 * @author limenghua
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("de")
public class DoctorInfoRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(DoctorInfoRepositoryTest.class);
    @Autowired
    private DoctorInfoRepository doctorInfoRepository;

    @Before
    public void pre(){
        logger.info("Test个龟孙,加载不了配置中心的内容");
    }

    @Test
    public void findByIdTest() {
        String id = "0000000051e758500151f2000b770005";
        DoctorInfo info = doctorInfoRepository.findById(id);
        logger.info(info.toString());
    }

}
