package com.wondersgroup.healthcloud.services.diabetes;

import com.wondersgroup.healthcloud.jpa.entity.diabetes.DiabetesAssessment;
import com.wondersgroup.healthcloud.services.diabetes.dto.DiabetesAssessmentDTO;

import java.util.List;
import java.util.Map;

/**
 * Created by zhuchunliu on 2016/12/6.
 */
public interface DiabetesAssessmentService {
    /**
     * 患病风险评估
     * @param assessment
     * @return
     */
    Integer sicken(DiabetesAssessment assessment);

    /**
     * 肾病风险评估
     * @param assessment
     * @return
     */
    Integer kidney(DiabetesAssessment assessment);

    /**
     * 眼病风险评估
     * @param assessment
     * @return
     */
    Integer eye(DiabetesAssessment assessment);

    /**
     * 足部风险评估
     * @param assessment
     * @return
     */
    Integer foot(DiabetesAssessment assessment);

    /**
     * 高危筛查列表
     * @param pageNo
     * @param pageSize
     * @param param
     * @return
     */
    List<DiabetesAssessmentDTO> findAssessment(Integer pageNo, Integer pageSize, String name);

    /**
     * 高危筛查列表数
     * @return
     */
    Integer findAssessmentTotal(String name);

    /**
     * 高危筛查列表推送
     * @param ids
     * @return
     */
    Boolean remind(String ids ,String doctorId);

    /**
     * 获取最后一次评估结果
     * @param uid
     * @return
     */
    Map<String, Object> getLastAssessmentResult(String uid);
}
