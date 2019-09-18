package seckill_swj.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import seckill_swj.domain.User;
import seckill_swj.rabbitmq.MQSender;
import seckill_swj.redis.RedisService;
import seckill_swj.redis.UserKey;
import seckill_swj.result.CodeMsg;
import seckill_swj.result.Result;
import seckill_swj.service.UserService;

@Controller
@RequestMapping("/demo")
public class SampleController {
	@Autowired
	UserService userService;
	@Autowired
	RedisService redisService;
	
	@Autowired
	MQSender sender;
	
	@RequestMapping("/mq")
	@ResponseBody
	public Result<String> mq(){
		sender.send("hello,world!");
		return Result.success("hello");
	}
	@RequestMapping("/mq/topic")
	@ResponseBody
	public Result<String> topic(){
		sender.sendTopic("hello,lalalalalalala");
		return Result.success("topic exchange working!");
	}
	@RequestMapping("/mq/fanout")
	@ResponseBody
	public Result<String> fanout(){
		sender.sendFanout("hello,papappapapapa");
		return Result.success("fanout exchange working!");
	}
	@RequestMapping("/mq/headers")
	@ResponseBody
	public Result<String> headers(){
		sender.sendHeaders("hello,headers");
		return Result.success("headers exchange working!");
	}
	
	
	@RequestMapping("/hello")
	String home() {
		return "Hello World!";
	}
	//1.rest api json输出  2.页面
	@RequestMapping("/error")
	@ResponseBody
	public Result<String> error() {
		return Result.error(CodeMsg.SESSION_ERROR);
		//return new Result(0,"success","hello,world");
	}
	
	@RequestMapping("/hello/thymeleaf")
	public String thymeleaf(Model model) {
		model.addAttribute("name", "南山临时工");
		return "hello";
	}
	@RequestMapping("/db/get")
	@ResponseBody
	public Result<User> dbGet() {
		User user = userService.getById(1);
		return Result.success(user);
	}
	@RequestMapping("/db/tx")
	@ResponseBody
	public Result<Boolean> dbTx() {
		userService.tx();
		return Result.success(true);
	}
	
	
	
	@RequestMapping("/redis/get")
	@ResponseBody
	public Result<User> redisGet() {
		User user = redisService.get(UserKey.getById,""+1,User.class);
		return Result.success(user);
	}
	
	@RequestMapping("/redis/set")
	@ResponseBody
	public Result<Boolean> redisSet() {
		User user = new User( );
		user.setId(1);
		user.setName("11111111111111");
		redisService.set(UserKey.getById,""+1,String.class);
		return Result.success(true);
	}
}
