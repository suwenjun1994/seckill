package seckill_swj.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import seckill_swj.dao.GoodsDao;
import seckill_swj.dao.OrderDao;
import seckill_swj.dao.UserDao;
import seckill_swj.domain.OrderInfo;
import seckill_swj.domain.SeckillOrder;
import seckill_swj.domain.SeckillUser;
import seckill_swj.domain.User;
import seckill_swj.redis.OrderKey;
import seckill_swj.redis.RedisService;
import seckill_swj.vo.GoodsVo;

@Service
public class OrderService {
	@Autowired
	OrderDao orderDao;
	
	@Autowired
	RedisService redisService;

	public SeckillOrder getSeckillOrderByUserIdGoodsId(long userId, long goodsId) {
		redisService.get(OrderKey.getSeckillOrderByUidGid, ""+userId+"_"+goodsId, SeckillOrder.class);
		return orderDao.getSeckillOrderByUserIdGoodsId(userId, goodsId);
	}

	public OrderInfo getOrderById(long orderId) {
		return orderDao.getOrderById(orderId);
	}

	@Transactional
	public OrderInfo createOrder(SeckillUser user, GoodsVo goods) {
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setCreateDate(new Date());
		orderInfo.setDeliveryAddrId(0L);
		orderInfo.setGoodsCount(1);
		orderInfo.setGoodsId(goods.getId());
		orderInfo.setGoodsName(goods.getGoodsName());
		orderInfo.setGoodsPrice(goods.getSeckillPrice());
		orderInfo.setOrderChannel(1);
		orderInfo.setStatus(0);
		orderInfo.setUserId(user.getId());
		orderDao.insert(orderInfo);
		SeckillOrder seckillOrder = new SeckillOrder();
		seckillOrder.setGoodsId(goods.getId());
		seckillOrder.setUserId(user.getId());
		seckillOrder.setOrderId(orderInfo.getId());
		orderDao.insertSeckillOrder(seckillOrder);
		redisService.set(OrderKey.getSeckillOrderByUidGid, user.getId()+"_"+goods.getId(), seckillOrder);
		return orderInfo;
	}

}
