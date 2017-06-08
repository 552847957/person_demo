package doctor;

import com.wondersgroup.healthcloud.api.configurations.Application;
import com.wondersgroup.healthcloud.jpa.entity.diabetes.DoctorTubeSignUser;
import com.wondersgroup.healthcloud.jpa.repository.diabetes.DoctorTubeSignUserRepository;
import com.wondersgroup.healthcloud.services.disease.DoctorTubeSignUserService;
import com.wondersgroup.healthcloud.services.disease.dto.ResidentCondition;
import com.wondersgroup.healthcloud.services.disease.dto.ResidentInfoDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
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
    @Autowired
    private DoctorTubeSignUserService doctorTubeSignUserService;

    //@Test
//    public void getAllTest() {
//        List<DoctorTubeSignUser> doctorTubeSignUserList = doctorTubeSignUserRepository.getAll();
//        logger.info(String.format("共查询到[%d条]数据", doctorTubeSignUserList.size()));
//        for (DoctorTubeSignUser doctorTubeSignUser : doctorTubeSignUserList) {
//            logger.info(doctorTubeSignUser.toString());
//        }
//
//        doctorTubeSignUserRepository.findAll();
//    }

    @Test
    public void search() {
        ResidentCondition residentInfoDto = new ResidentCondition();
        // apo,hyp,diabetes
        residentInfoDto.setDiseaseType("apo,hyp,diabetes");
        // disease  risk  healthy
        // residentInfoDto.setPeopleType("healthy");
        residentInfoDto.setSigned(1);
        Page<DoctorTubeSignUser> page = doctorTubeSignUserService.search(residentInfoDto, 0);
        List<DoctorTubeSignUser> list = page.getContent();
        logger.info(String.format("共查询到%d条数据", list.size()));

        List<ResidentInfoDto> dtoList = doctorTubeSignUserService.pageDataToDtoList(page);
        for (ResidentInfoDto infoDto : dtoList) {
            logger.info(infoDto.toString());
        }
        logger.info(page.toString());
    }


}
