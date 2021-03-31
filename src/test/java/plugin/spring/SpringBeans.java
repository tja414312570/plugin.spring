package plugin.spring;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("testSpringBeans")
public class SpringBeans {
	@Autowired(required = false)
	private Logger logger;
	public SpringBeans() {
		System.out.println("实例化");
	}
	@PostConstruct
	public void init() {
		logger.debug("实例化后执行了我"+this);
	}
	@PreDestroy
	public void destory() {
		logger.debug("实例销毁");
	}
}
