package seckill_swj.redis;

public class GoodsKey extends BasePrefix{

	public GoodsKey(int expireSeconds,String prefix) { //0代表永不过期
		super(expireSeconds,prefix);
		
	}


	public static GoodsKey getGoodsList = new GoodsKey(60,"gl");
	public static GoodsKey getGoodsDetail = new GoodsKey(60,"gd");
	public static GoodsKey getSeckillGoodsStock = new GoodsKey(0,"gs");
	

}
