package czw.service;

import czw.interfaceMode.UserInterface;
import spring.annotation.Autowired;
import spring.annotation.Component;
import spring.annotation.ComponetScan;

@Component
public class OrderService {
    @Autowired
    UserInterface userService;
    public void test(){
        userService.test();
        System.out.println("执行orderservice test方法");
    }
}
