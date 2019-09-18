package seckill_swj.controller;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import seckill_swj.redis.RedisService;
import seckill_swj.result.CodeMsg;
import seckill_swj.result.Result;
import seckill_swj.service.SeckillUserService;
import seckill_swj.util.ValidatorUtil;
import seckill_swj.vo.LoginVo;

@Controller
@RequestMapping("/login")
public class LoginController {
	@Autowired
	SeckillUserService seckillUserService;
	@Autowired
	RedisService redisService;

	private static Logger log = LoggerFactory.getLogger(LoginController.class);

	@RequestMapping("/to_login")
	// @ResponseBody
	public String toLogin() {
		return "login";
	}

	// 1.rest api json输出 2.页面
	@RequestMapping("/do_login")
	@ResponseBody
	public Result<Boolean> doLogin(HttpServletResponse response, @Valid LoginVo loginVo) {
		log.info(loginVo.toString());
		// //参数校验
		// String passInput = loginVo.getPassword();
		// String mobile = loginVo.getMobile();
		// if(StringUtils.isEmpty(passInput)) {
		// return Result.error(CodeMsg.PASSWORD_EMPTY);
		// }
		// if(StringUtils.isEmpty(mobile)) {
		// return Result.error(CodeMsg.MOBILE_EMPTY);
		// }
		// if(!ValidatorUtil.isMobile(mobile)) {
		// return Result.error(CodeMsg.MOBILE_ERROR);
		// }
		// 登录
		if(seckillUserService.login(response,loginVo)) {
			return Result.success(true);
		}
		return Result.error(CodeMsg.SERVER_ERROR);
	}
}
