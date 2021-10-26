package czw.service;

import spring.annotation.Component;
import spring.interfaceModel.InitializingBean;
@Component
public class CzwInitalizing implements InitializingBean {
    @Override
    public void afterPropertiesSet() {
        System.out.println("执行了CzwInitalizing");

    }
}
