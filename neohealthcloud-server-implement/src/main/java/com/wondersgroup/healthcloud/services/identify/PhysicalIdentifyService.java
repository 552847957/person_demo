package com.wondersgroup.healthcloud.services.identify;

import com.wondersgroup.healthcloud.jpa.entity.identify.HealthQuestion;

public interface PhysicalIdentifyService {
	/**
	 * 提交中医体质标识
	 * @param registerid
	 *
	 * @param content
	 * @return
	 */
	String physiqueIdentify(String registerid, String content);

	/**
	 * 根据用户获取最近一次中医体质辨识结果
	 * @param registerid
	 * @return
	 */
	HealthQuestion getRecentPhysicalIdentify(String registerid);
}
