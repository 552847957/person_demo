package com.wondersgroup.healthcloud.services.diabetes;

import com.wondersgroup.healthcloud.jpa.entity.diabetes.DiabetesAssessment;

import javax.persistence.criteria.CriteriaBuilder;

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
}
