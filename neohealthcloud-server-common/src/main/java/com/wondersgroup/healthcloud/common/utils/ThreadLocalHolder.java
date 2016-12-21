package com.wondersgroup.healthcloud.common.utils;

import org.springframework.core.NamedThreadLocal;

public class ThreadLocalHolder {

	private static final ThreadLocal<Boolean> versionType = new NamedThreadLocal<Boolean>("versions type");

	public static void setVersionType(boolean isStandard) {
		versionType.set(isStandard);
	}

	public static Boolean getVersionType() {
		Boolean isStandard = versionType.get();
		return isStandard == null ? false : isStandard;
	}

}
