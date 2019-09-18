package seckill_swj.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import seckill_swj.dao.UserDao;
import seckill_swj.domain.User;

@Service
public class UserService {
	@Autowired
	UserDao userDao;
	
	public User getById(int id) {
		return userDao.getById(id);
	}
	
	@Transactional
	public Boolean tx() {
		User u1 = new User();
		 u1.setId(22);
		 u1.setName("2222222");
		userDao.insert(u1);
		
		User u2 = new User();
		 u1.setId(1111);
		 u1.setName("1111111");
		userDao.insert(u2);
		return true;
	}
}
