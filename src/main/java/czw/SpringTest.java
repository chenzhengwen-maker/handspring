package czw;

import czw.interfaceMode.UserInterface;
import czw.service.OrderService;
import czw.service.UserService;
import spring.CzwApplicaitonContext;

public class SpringTest {
    public static void main(String[] args) {
        CzwApplicaitonContext czwApplicaitonContext = new CzwApplicaitonContext(AppConfig.class);
        OrderService orderService = (OrderService) czwApplicaitonContext.getBean("orderService");
        orderService.test();
        /*UserInterface userInterface = (UserInterface) czwApplicaitonContext.getBean("userService");
        userInterface.test();*/
    }
}
