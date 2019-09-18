package seckill_swj.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import seckill_swj.domain.Goods;
import seckill_swj.domain.SeckillOrder;
import seckill_swj.domain.SeckillUser;
import seckill_swj.redis.RedisService;
import seckill_swj.result.CodeMsg;
import seckill_swj.result.Result;
import seckill_swj.service.GoodsService;
import seckill_swj.service.OrderService;
import seckill_swj.service.SeckillService;
import seckill_swj.service.SeckillUserService;
import seckill_swj.vo.GoodsVo;

@Service
public class MQReceiver {
	@Autowired
	SeckillService seckillService;
	
	@Autowired
	GoodsService goodsService;
	
	@Autowired
	SeckillUserService seckillUserService;
	@Autowired
	RedisService redisService;
	
	@Autowired
	OrderService orderService;
	
	private static Logger log = LoggerFactory.getLogger(MQReceiver.class);
	
	//direct
	@RabbitListener(queues=MQConfig.QUEUE)
	public void receive(String message) {
		log.info("receive message:"+message);
	}
	@RabbitListener(queues=MQConfig.SECKILL_QUEUE)
	public void receiveSeckill(String message) {
		log.info("receive secekill message:"+message);
		SeckillMessage sm = RedisService.stringToBean(message, SeckillMessage.class);
		SeckillUser user = sm.getUser();
		long goodsId = sm.getGoodsId();
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		int stock = goods.getStockCount();
		if(stock <= 0) {
			return;
		}
		//判断是否已经秒杀到了
		SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
		if(order != null) {
			return;
		}
		//减库存，下订单，写入秒杀订单
		seckillService.seckill(user, goods);
	}
	//topic
	@RabbitListener(queues=MQConfig.TOPIC_QUEUE1)
	public void receiveTopic1(String message) {
		log.info("topic queue1 receive message:"+message);
	}
	@RabbitListener(queues=MQConfig.TOPIC_QUEUE2)
	public void receiveTopic2(String message) {
		log.info("topic queue2 receive message:"+message);
	}
	
	//fanout
	//使用的是topic队列
	
	//headers
	@RabbitListener(queues=MQConfig.HEADERS_QUEUE)
	public void receiveHeaders(byte[] message) {
		log.info("headers queue receive message:"+new String(message));
	}
}
