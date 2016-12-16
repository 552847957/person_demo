package com.wondersgroup.healthcloud.common.utils;

import java.util.Random;

public class RandomUtil {

	public static int randomInt(int min, int max) {
		Random random = new Random();
		return random.nextInt(max - min) + min;
	}

}
