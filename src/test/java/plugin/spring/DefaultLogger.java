package plugin.spring;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.plugin.autowired.plugin.CustomProxy;
import com.yanan.framework.plugin.autowired.plugin.WiredStackContext;

@Register(register = Logger.class)
public class DefaultLogger implements CustomProxy<Logger>{
	@Override
	public Logger getInstance() {
		System.out.println("注入地方:"+WiredStackContext.stackString());
		Logger logger = LoggerFactory.getLogger(WiredStackContext.getRegisterDefintion().getRegisterClass());
		return logger;
	}
}
