package czw.service;

import czw.interfaceMode.UserInterface;
import spring.annotation.Autowired;
import spring.annotation.Component;

@Component
public class UserService implements UserInterface {
    public void test(){
        System.out.println("执行了userservice的test方法");
    };
}
