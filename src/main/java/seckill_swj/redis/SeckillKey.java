package seckill_swj.redis;

public class SeckillKey extends BasePrefix{

	public SeckillKey(String prefix) { //0代表永不过期
		super(prefix);
		
	}


	public static SeckillKey isGoodsOver = new SeckillKey("go");
	
}
