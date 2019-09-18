package seckill_swj.redis;

public interface KeyPrefix {
	
	public int expireSeconds();
	public String getPrefix();
	
}
