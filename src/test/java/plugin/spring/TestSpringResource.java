package plugin.spring;

import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.plugin.spring.SpringBeanResourceDecoder;
import com.yanan.plugin.spring.SpringBeansResource;
import com.yanan.utils.resource.Resource;

public class TestSpringResource {
	public static void main(String[] args) {
		Resource resource = new SpringBeansResource("classpath:**");
		PlugsFactory.getInstance().addScanPath(SpringBeanResourceDecoder.class);
		PlugsFactory.getInstance().addScanPath(TestSpringResource.class);
		PlugsFactory.getInstance().addResource(resource);
		PlugsFactory.init();
		SpringBeans springBeans = PlugsFactory.getPluginsInstance("testSpringBeans");
		System.out.println(springBeans);
		System.out.println("----");
		springBeans = PlugsFactory.getPluginsInstance("testSpringBeans");
		System.out.println(springBeans);
	}
}
