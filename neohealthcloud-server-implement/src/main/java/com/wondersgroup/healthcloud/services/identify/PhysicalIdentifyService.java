package com.wondersgroup.healthcloud.services.identify;

public interface PhysicalIdentifyService {
	/**
	 * 提交中医体质标识
	 * @param registerid
	 * @param content
	 * @return
	 */
	String physiqueIdentify(String registerid, String content);

	/**
	 * 根据用户获取最近一次中医体质辨识结果
	 * @param registerid
	 * @return  如：阴虚质,基本是气虚质
	 */
	String getRecentPhysicalIdentify(String registerid);
}
