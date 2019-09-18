package seckill_swj.vo;

import seckill_swj.domain.SeckillUser;

public class GoodsDetailVo {
	private GoodsVo goods;
	private SeckillUser seckillUser;
	private int seckillStatus = 0;
	private int remainSeconds = 0;
	
	public SeckillUser getSeckillUser() {
		return seckillUser;
	}
	public void setSeckillUser(SeckillUser user) {
		this.seckillUser = user;
	}
	public GoodsVo getGoods() {
		return goods;
	}
	public void setGoods(GoodsVo goods) {
		this.goods = goods;
	}
	public int getSeckillStatus() {
		return seckillStatus;
	}
	public void setSeckillStatus(int seckillStatus) {
		this.seckillStatus = seckillStatus;
	}
	public int getRemainSeconds() {
		return remainSeconds;
	}
	public void setRemainSeconds(int remainSeconds) {
		this.remainSeconds = remainSeconds;
	}
	
}
