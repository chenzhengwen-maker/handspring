package czw.service;

import spring.annotation.Component;
import spring.interfaceModel.BeanPostProcessor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Component
public class CzwBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if(beanName.equals("orderService")){
            System.out.println("初始化前我执行到了orderService");
        }
        if(beanName.equals("userService")){
            System.out.println("初始化前我执行到了userService");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if(beanName.equals("orderService")){
            System.out.println("初始化后执行了orderService");
        }
        if(beanName.equals("userService")){
            System.out.println("初始化后我执行到了userService");
            Object proxyInstance = Proxy.newProxyInstance(CzwBeanPostProcessor.class.getClassLoader(), bean.getClass().getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    System.out.println("执行切面逻辑");
                    return method.invoke(bean,args);
                }
            });
            return proxyInstance;
        }
        return bean;
    }
}
