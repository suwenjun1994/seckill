package seckill_swj.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import seckill_swj.domain.OrderInfo;
import seckill_swj.domain.SeckillOrder;
import seckill_swj.domain.SeckillUser;
import seckill_swj.redis.RedisService;
import seckill_swj.redis.SeckillKey;
import seckill_swj.vo.GoodsVo;

@Service
public class SeckillService {
	
	@Autowired
	GoodsService goodsService;
	@Autowired
	OrderService orderService;
	@Autowired
	RedisService redisService;
	@Transactional
	public OrderInfo seckill(SeckillUser user, GoodsVo goods) {
		//减库存，下订单，写入秒杀订单
		boolean success = goodsService.reduceStock(goods);
		if(success) {
			return orderService.createOrder(user,goods);
		}else {
			setGoodsOver(goods.getId());
			return null;  
		}
		
	}

	
	public long getSeckillResult(Long userId, long goodsId) {
		SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(userId, goodsId);
		if(order != null) {//秒杀成功
			return order.getOrderId();
		}else {
			boolean isOver = getGoodsOver(goodsId);
			if(isOver) {
				return -1;
			}else {
				return 0;
			}
		}
	}

	private void setGoodsOver(Long goodsId) {
		redisService.set(SeckillKey.isGoodsOver, ""+goodsId, true);
		
	}

	private boolean getGoodsOver(long goodsId) {
		return redisService.exists(SeckillKey.isGoodsOver, ""+goodsId);
	}
	

}
