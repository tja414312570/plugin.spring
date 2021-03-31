package com.yanan.plugin.spring;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.stereotype.Service;

import com.typesafe.config.ConfigFactory;
import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.plugin.builder.PluginDefinitionBuilderFactory;
import com.yanan.framework.plugin.decoder.ResourceDecoder;
import com.yanan.framework.plugin.definition.RegisterDefinition;
import com.yanan.utils.resource.scanner.PackageScanner;

/**
 * spring资源扫描
 * @author yanan
 *
 */
@Register(attribute="SpringBeansResource",id="springBeanResourceDecoder")
public class SpringBeanResourceDecoder implements ResourceDecoder<SpringBeansResource>{
	
	@Override
	public void decodeResource(PlugsFactory factory,SpringBeansResource resource) {
		String scanExpress = resource.getPath();
		PackageScanner scanner = new PackageScanner();
		scanner.setScanPath(scanExpress);
		scanner.doScanner((cls) -> tryDecodeDefinition(cls));
	}
	private void tryDecodeDefinition(Class<?> cls) {
		if(cls.getAnnotation(Service.class)!= null) {
			try {
				Service service = cls.getAnnotation(Service.class);
				Map<String,Object> config = new HashMap<>();
				config.put(PluginDefinitionBuilderFactory.CONFIG_CLASS,cls.getName());
				config.put(PluginDefinitionBuilderFactory.CONFIG_ID,service.value());
				decodeExecuteMethods(cls,config);
				RegisterDefinition registerDefinition = PluginDefinitionBuilderFactory.buildRegisterDefinitionByConfig(ConfigFactory.parseMap(config));
				PlugsFactory.getInstance().addRegisterDefinition(registerDefinition);
			}catch(Throwable t) {
				t.printStackTrace();
			}
		}
	}
	private void decodeExecuteMethods(Class<?> cls, Map<String, Object> config) {
		Method[] methods = cls.getDeclaredMethods();
		for(Method method : methods) {
			if(method.getAnnotation(PostConstruct.class) != null) {
				config.put(PluginDefinitionBuilderFactory.CONFIG_INIT, method.getName());
			}
			if(method.getAnnotation(PreDestroy.class) != null) {
				config.put(PluginDefinitionBuilderFactory.CONFIG_DESTORY, method.getName());
			}
		}
	}
}
