package seckill_swj.redis;

public class SeckillUserKey extends BasePrefix {
	
	public static final int TOKEN_EXPIRE =3600*24*2;
	public SeckillUserKey(int expireSeconds, String prefix) { // 0代表永不过期
		super(expireSeconds,prefix);

	}

	public static SeckillUserKey token = new SeckillUserKey(TOKEN_EXPIRE,"tk");
	public static SeckillUserKey getById = new SeckillUserKey(0,"id");

}
