package seckill_swj.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import seckill_swj.dao.SeckillUserDao;
import seckill_swj.domain.SeckillUser;
import seckill_swj.exception.GlobalException;
import seckill_swj.redis.RedisService;
import seckill_swj.redis.SeckillUserKey;
import seckill_swj.result.CodeMsg;
import seckill_swj.util.MD5Util;
import seckill_swj.util.UUIDUtil;
import seckill_swj.vo.LoginVo;

@Service
public class SeckillUserService implements SeckillUserDao {

	public static final String COOKIE_NAME_TOKEN = "token";

	@Autowired
	SeckillUserDao seckillUserDao;

	@Autowired
	RedisService redisService;

	@Override
	public SeckillUser getById(long id) {
		// 取缓存
		SeckillUser seckillUser = redisService.get(SeckillUserKey.getById, "" + id, SeckillUser.class);
		if (seckillUser != null) {
			return seckillUser;
		}
		// 取数据库
		seckillUser = seckillUserDao.getById(id);
		// 加入缓存，方便下次访问
		if (seckillUser != null) {
			redisService.set(SeckillUserKey.getById, "" + id, seckillUser);
		}

		return seckillUser;
	}

	// 更改密码
	public boolean updatePassword(String token, long id, String formPass) {
		// 取user
		SeckillUser seckillUser = getById(id);
		if (seckillUser == null) {
			throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
		}
		// 更新数据库
		SeckillUser toBeUpdate = new SeckillUser();
		toBeUpdate.setId(id);
		toBeUpdate.setPassword(MD5Util.formPassToDBPass(formPass, seckillUser.getSalt()));
		seckillUserDao.update(toBeUpdate);
		// 处理缓存
		redisService.delete(SeckillUserKey.getById, "" + id);
		seckillUser.setPassword(toBeUpdate.getPassword());
		redisService.set(SeckillUserKey.token, token, toBeUpdate);
		return true;
	}

	public boolean login(HttpServletResponse response, LoginVo loginVo) {
		if (loginVo == null) {
			throw new GlobalException(CodeMsg.SERVER_ERROR);
		}
		String mobile = loginVo.getMobile();
		String formPass = loginVo.getPassword();
		// 判断手机号是否存在
		SeckillUser user = getById(Long.parseLong(mobile));
		if (user == null) {
			throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
		}
		// 验证密码
		String dbPass = user.getPassword();
		String salt = user.getSalt();
		String calcPass = MD5Util.formPassToDBPass(formPass, salt);
		if (!calcPass.equals(dbPass)) {
			throw new GlobalException(CodeMsg.PASSWORD_ERROR);
		}
		// 生成Cookie
		String token = UUIDUtil.uuid();
		addCookie(response, token, user);
		return true;
	}

	private void addCookie(HttpServletResponse response, String token, SeckillUser user) {

		redisService.set(SeckillUserKey.token, token, user);
		Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
		cookie.setMaxAge(SeckillUserKey.token.expireSeconds());
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	public SeckillUser getByToken(HttpServletResponse response, String token) {
		if (StringUtils.isEmpty(token)) {
			return null;
		}
		SeckillUser user = redisService.get(SeckillUserKey.token, token, SeckillUser.class);
		// 延长有效期
		if (user != null) {

			addCookie(response, token, user);
		}
		return user;
	}

	@Override
	public void update(SeckillUser toBeUpdate) {
		seckillUserDao.update(toBeUpdate);
		
	}

}
