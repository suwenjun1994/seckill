package seckill_swj.controller;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import seckill_swj.domain.OrderInfo;
import seckill_swj.domain.SeckillUser;
import seckill_swj.redis.RedisService;
import seckill_swj.result.CodeMsg;
import seckill_swj.result.Result;
import seckill_swj.service.GoodsService;
import seckill_swj.service.OrderService;
import seckill_swj.service.SeckillUserService;
import seckill_swj.util.ValidatorUtil;
import seckill_swj.vo.GoodsVo;
import seckill_swj.vo.LoginVo;
import seckill_swj.vo.OrderDetailVo;

@Controller
@RequestMapping("/order")
public class OrderController {
	@Autowired
	SeckillUserService seckillUserService;
	@Autowired
	RedisService redisService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	GoodsService goodsService;

	private static Logger log = LoggerFactory.getLogger(OrderController.class);
	
	@RequestMapping("/detail")
	@ResponseBody
	public Result<OrderDetailVo> info(Model model,SeckillUser user,@RequestParam("orderId")long orderId){
		if(user == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		OrderInfo orderInfo = orderService.getOrderById(orderId);
		if(orderInfo == null) {
			return Result.error(CodeMsg.ORDER_NOT_EXIST);
		}
		long goodsId = orderInfo.getGoodsId();
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		OrderDetailVo orderDetailVo = new OrderDetailVo();
		orderDetailVo.setGoods(goods);
		orderDetailVo.setOrderInfo(orderInfo);
		return Result.success(orderDetailVo);
		
	}
	
}
