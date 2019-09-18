 package seckill_swj.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import seckill_swj.domain.SeckillUser;
import seckill_swj.redis.GoodsKey;
import seckill_swj.redis.RedisService;
import seckill_swj.result.Result;
import seckill_swj.service.GoodsService;
import seckill_swj.service.SeckillUserService;
import seckill_swj.vo.GoodsDetailVo;
import seckill_swj.vo.GoodsVo;

@Controller
@RequestMapping("/goods")
public class GoodsController {

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	ThymeleafViewResolver thymeleafViewResolver;

	@Autowired
	GoodsService goodsService;

	@Autowired
	SeckillUserService seckillUserService;
	@Autowired
	RedisService redisService;

	private static Logger log = LoggerFactory.getLogger(GoodsController.class);

	@RequestMapping(value = "/to_list", produces = "text/html")
	@ResponseBody
	public String toList(HttpServletRequest request, HttpServletResponse response, Model model, SeckillUser user) {
		// 1.从缓存里面取
				String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
				if (!StringUtils.isEmpty(html)) {
					return html;
				}
		model.addAttribute("user", user);
		// 查询商品列表
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		model.addAttribute("goodsList", goodsList);
		// return "goods_list";

		
		// 2.若取不到，则手动渲染
		SpringWebContext ctx = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(),
				model.asMap(), applicationContext);
		html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
		if (!StringUtils.isEmpty(html)) {
			redisService.set(GoodsKey.getGoodsList, "", html);
		}
		return html;
	}
	
	@RequestMapping(value = "/detail/{goodsId}")
	@ResponseBody
	public Result<GoodsDetailVo> detail(HttpServletRequest request, HttpServletResponse response, Model model, SeckillUser user,
			@PathVariable("goodsId") long goodsId) {
		
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
	

		long startAt = goods.getStartDate().getTime();
		long endAt = goods.getEndDate().getTime();
		long now = System.currentTimeMillis();

		int seckillStatus = 0;
		int remainSeconds = 0;
		if (now < startAt) {// 秒杀还没开始，倒计时
			seckillStatus = 0;
			remainSeconds = (int) ((startAt - now) / 1000);
		} else if (now > endAt) {// 秒杀已经结束
			seckillStatus = 2;
			remainSeconds = -1;
		} else {// 秒杀进行中
			seckillStatus = 1;
			remainSeconds = 0;
		}
		GoodsDetailVo vo = new GoodsDetailVo();
		vo.setSeckillUser(user);
		vo.setGoods(goods);
		vo.setRemainSeconds(remainSeconds);
		vo.setSeckillStatus(seckillStatus);
		return Result.success(vo);
	}

	@RequestMapping(value = "/to_detail/{goodsId}", produces = "text/html")
	@ResponseBody
	public String toDetail(HttpServletRequest request, HttpServletResponse response, Model model, SeckillUser user,
			@PathVariable("goodsId") long goodsId) {
		// 1.从缓存里面取
				String html = redisService.get(GoodsKey.getGoodsDetail, "", String.class);
				if (!StringUtils.isEmpty(html)) {
					return html;
				}
		// snow
		model.addAttribute("user", user);
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		model.addAttribute("goods", goods);

		long startAt = goods.getStartDate().getTime();
		long endAt = goods.getEndDate().getTime();
		long now = System.currentTimeMillis();

		int seckillStatus = 0;
		int remainSeconds = 0;
		if (now < startAt) {// 秒杀还没开始，倒计时
			seckillStatus = 0;
			remainSeconds = (int) ((startAt - now) / 1000);
		} else if (now > endAt) {// 秒杀已经结束
			seckillStatus = 2;
			remainSeconds = -1;
		} else {// 秒杀进行中
			seckillStatus = 1;
			remainSeconds = 0;
		}
		model.addAttribute("seckillStatus", seckillStatus);
		model.addAttribute("remainSeconds", remainSeconds);
		// return "goods_detail";
		
		//2.若无法从缓存中获取，则手动渲染
		SpringWebContext ctx = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(),
				model.asMap(), applicationContext);
		html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
		if(!StringUtils.isEmpty(html)) {
			redisService.set(GoodsKey.getGoodsDetail, "", html);
		}
		return html;
	}
	
	

}
