package seckill_swj.controller;

import java.util.List;

import javax.security.auth.message.callback.PrivateKeyCallback.Request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import seckill_swj.domain.OrderInfo;
import seckill_swj.domain.SeckillOrder;
import seckill_swj.domain.SeckillUser;
import seckill_swj.rabbitmq.MQSender;
import seckill_swj.rabbitmq.SeckillMessage;
import seckill_swj.redis.GoodsKey;
import seckill_swj.redis.RedisService;
import seckill_swj.result.CodeMsg;
import seckill_swj.result.Result;
import seckill_swj.service.GoodsService;
import seckill_swj.service.OrderService;
import seckill_swj.service.SeckillService;
import seckill_swj.service.SeckillUserService;
import seckill_swj.vo.GoodsVo;

@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

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

	@Autowired
	MQSender sender;

	private static Logger log = LoggerFactory.getLogger(SeckillController.class);

	/**
	 * 系统初始化
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		if (goodsList == null) {
			return;
		} else {
			for (GoodsVo goods : goodsList) {
				redisService.set(GoodsKey.getSeckillGoodsStock, "" + goods.getId(), goods.getStockCount());
			}
		}

	}

	@RequestMapping(value="/do_seckill",method=RequestMethod.POST)
	@ResponseBody
	public Result<Integer> seckill(Model model, SeckillUser user,@RequestParam("goodsId") long goodsId) {
		model.addAttribute("user", user);
		if(user == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
	
		long stock = redisService.decr(GoodsKey.getSeckillGoodsStock, ""+goodsId);
		if(stock < 0) {
			return Result.error(CodeMsg.SECKILL_OVER);
		}
		//判断是否已经秒杀到了
		SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(user.getId(),goodsId);
		if(order != null) {
			return Result.error(CodeMsg.REPEATE_SECKILL);
		}
		// 入队
		SeckillMessage sm = new SeckillMessage();
		sm.setUser(user);
		sm.setGoodsId(goodsId);
		sender.sendSeckillMessage(sm);
		return Result.success(0);//排队中
	}
	/**
	 * orderId:成功
	 * -1:秒杀失败
	 * 0:排队中 
	 * @param model
	 * @param user
	 * @param goodsId
	 * @return
	 */
	@RequestMapping(value="/result",method=RequestMethod.GET)
	@ResponseBody
	public Result<Long> seckillResult(Model model, SeckillUser user,@RequestParam("goodsId") long goodsId) {
		model.addAttribute("user", user);
		if(user == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		long result =seckillService.getSeckillResult(user.getId(),goodsId);
		return Result.success(result);
	}



}
