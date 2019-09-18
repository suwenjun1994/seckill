package seckill_swj.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import seckill_swj.redis.RedisService;

@Service
public class MQSender {
	private static Logger log = LoggerFactory.getLogger(MQReceiver.class);
	@Autowired
	AmqpTemplate amqpTemplate;

	public void send(Object message) {
		String msg = RedisService.beanToString(message);
		log.info("send message:" + msg);
		amqpTemplate.convertAndSend(MQConfig.QUEUE, msg);
	}

	public void sendTopic(Object message) {
		String msg = RedisService.beanToString(message);
		log.info("send topic message:" + msg);
		amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key1", msg + "1");
		amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key2", msg + "2");
	}

	public void sendFanout(Object message) {
		String msg = RedisService.beanToString(message);
		log.info("send fanout message:" + msg);
		amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE, "", msg + "1");
		amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE, "", msg + "2");
	}
	
	public void sendHeaders(Object message) {
		String msg = RedisService.beanToString(message);
		log.info("send fanout message:" + msg);
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.setHeader("header1", "value1");
		messageProperties.setHeader("header2", "value2");
		Message obj = new Message(msg.getBytes(), messageProperties);
		amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE, "", obj);
	}

	public void sendSeckillMessage(SeckillMessage sm) {
		String msg = RedisService.beanToString(sm);
		log.info("send message:"+ msg);
		amqpTemplate.convertAndSend(MQConfig.SECKILL_QUEUE, msg);
		
	}
}
