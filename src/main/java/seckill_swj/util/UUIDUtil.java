package seckill_swj.util;

import java.util.UUID;

public class UUIDUtil {
	public static String uuid() {
		//生成的uuid带-，用字符串将-置换掉
		return UUID.randomUUID().toString().replace("-","");
		
	}
}
