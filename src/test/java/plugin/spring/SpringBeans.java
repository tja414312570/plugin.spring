package plugin.spring;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.stereotype.Service;

@Service("testSpringBeans")
public class SpringBeans {
	public SpringBeans() {
		System.out.println("实例化");
	}
	@PostConstruct
	public void init() {
		System.out.println("实例化后执行了我"+this);
	}
	@PreDestroy
	public void destory() {
		System.out.println("实例销毁");
	}
}
