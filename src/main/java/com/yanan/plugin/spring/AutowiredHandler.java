package com.yanan.plugin.spring;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.plugin.annotations.Support;
import com.yanan.framework.plugin.autowired.plugin.WiredStackContext;
import com.yanan.framework.plugin.definition.RegisterDefinition;
import com.yanan.framework.plugin.exception.PluginRuntimeException;
import com.yanan.framework.plugin.handler.FieldHandler;
import com.yanan.framework.plugin.handler.HandlerSet;
import com.yanan.framework.plugin.handler.InstanceHandler;
import com.yanan.framework.plugin.handler.InvokeHandler;
import com.yanan.framework.plugin.handler.MethodHandler;

@Support(Autowired.class)
@Register(attribute = "*", description = "Service服务的注入")
public class AutowiredHandler implements InvokeHandler, FieldHandler, InstanceHandler{
	@Override
	public void before(MethodHandler methodHandler) {
		Autowired service;
		try {
			WiredStackContext.push(WiredStackContext.METHOD,methodHandler);
			Parameter[] parameters = methodHandler.getMethod().getParameters();
			Object[] arguments = methodHandler.getParameters();
			for (int i = 0; i < parameters.length; i++) {
				Parameter parameter = parameters[i];
				service = parameter.getAnnotation(Autowired.class);
				if (service != null) {
					if (arguments[i] == null) {
						Class<?> type = parameters[i].getType();
						Qualifier qualifier = parameter.getAnnotation(Qualifier.class);
						Object obj = findInstance(qualifier, service, type,parameters[i].getName(),"could not found bean id for \"" + ( qualifier==null?null:qualifier.value())
						+ "\" for parameter \"" + parameter.getName() + "\" for method \""
						+ methodHandler.getMethod().getName() + "\" for "
						+ methodHandler.getPlugsProxy().getRegisterDefinition().getRegisterClass());
						arguments[i] = obj;
					}
				}
			}
		} finally {
			WiredStackContext.pop();
		}

	}

	@Override
	public void after(MethodHandler methodHandler) {

	}

	@Override
	public void error(MethodHandler methodHandler, Throwable e) {
	}

	@Override
	public void before(RegisterDefinition registerDefinition, Class<?> plugClass, Constructor<?> constructor,
			Object... arguments) {
		System.out.println(this);
		Autowired service;
		try {
			WiredStackContext.push(WiredStackContext.CONSTRUCT,registerDefinition,plugClass,constructor,arguments);
			// 获取构造方法所有的参数
			Parameter[] parameters = constructor.getParameters();
			for (int i = 0; i < parameters.length; i++) {
				Parameter parameter = parameters[i];
				// 获取参数的Service注解
				service = parameter.getAnnotation(Autowired.class);
				if (service != null) {
					// 如果参数不为Null时，不注入此参数
					if (arguments[i] == null) {
						// 获取参数的类型
						Class<?> type = parameters[i].getType();
						Qualifier qualifier = parameter.getAnnotation(Qualifier.class);
						// 如果Service的注解具有id属性，说明为Bean注入
						Object obj = findInstance(qualifier, service, type,parameters[i].getName(),"could not found bean id for \"" + ( qualifier==null?null:qualifier.value())
						+ "\" type \"" + parameter.getType() + "\" for parameter \""
						+ parameter.getName() + "\" for construct \"" + constructor + "\" for "
						+ registerDefinition.getRegisterClass());
						arguments[i] = obj;
					}
				}
			}
		} finally {
			WiredStackContext.pop();
		}
	}

	@Override
	public void after(RegisterDefinition registerDefinition, Class<?> plugClass, Constructor<?> constructor,
			Object proxyObject, Object... args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void exception(RegisterDefinition registerDefinition, Class<?> plug, Constructor<?> constructor,
			Object proxyObject, PluginRuntimeException throwable, Object... args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void preparedField(RegisterDefinition registerDefinition, Object proxy, Object target, HandlerSet handlerSet,
			Field field) {
		try {
			WiredStackContext.push(WiredStackContext.FIELD,registerDefinition,proxy,target,handlerSet,field);
			Autowired autowired = handlerSet.getAnnotation(Autowired.class);
			Qualifier qualifier = registerDefinition.getRegisterClass().getAnnotation(Qualifier.class);
			try {
				field.setAccessible(true);
				Class<?> type = field.getType();
				Object obj = findInstance(qualifier, autowired, type,field.getName(),"could not found register or bean for field \"" + field.getName() + "\" type \""
						+ field.getType() + "\" for " + registerDefinition.getRegisterClass());
				field.set(target, obj);
				field.setAccessible(false);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException("failed to autowired service ! at class : "
						+ registerDefinition.getRegisterClass().getName() + "at field : " + field.getName(), e);
			}
		} finally {
			WiredStackContext.pop();
		}
	}

	private Object findInstance(Qualifier qualifier,Autowired autowired,Class<?> type, String argsName, String errMsg) {
		Object obj = null;
		if(qualifier != null) {
			if (type.isArray()) {
				obj = PlugsFactory.getPluginsInstanceListByAttribute(type, qualifier.value())
						.toArray();
			} else if (type.getClass().equals(List.class)) {
				obj = PlugsFactory.getPluginsInstanceListByAttribute(type, qualifier.value());
			}else{
				obj = PlugsFactory.getPluginsInstance(qualifier.value());
			}
			if (obj == null) {
				try {
					// from attris
					obj = PlugsFactory.getPluginsInstanceByAttributeStrict(type, qualifier.value());
				} catch (Throwable e) {
				}
			}
		}
		if(obj == null) {
			try {
				// from field name
				obj = PlugsFactory.getPluginsInstance(argsName);
			} catch (Throwable t) {
			}
			if (obj == null) {
				try {
					// from type
				obj = PlugsFactory.getPluginsInstance(type);
				} catch (Throwable e) {
				}
			}
		}
		if (obj == null && autowired.required()) {
			throw new PluginRuntimeException(errMsg);
		}
		return obj;
	}
}
