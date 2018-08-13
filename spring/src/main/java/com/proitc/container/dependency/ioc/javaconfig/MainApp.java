package com.proitc.container.dependency.ioc.javaconfig;

import com.proitc.bean.ComplexBean;
import com.proitc.bean.DatabaseService;
import com.proitc.bean.LoginService;
import com.proitc.bean.MailService;
import com.proitc.bean.RegisterService;
import com.proitc.bean.User;
import com.proitc.bean.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.Assert;

public class MainApp {

  private static final Logger log = LoggerFactory.getLogger(MainApp.class);

  public static void main(String[] args) {

    ApplicationContext context = new AnnotationConfigApplicationContext(JavaConfig.class);

    //Autowired
    UserManager userManager = context.getBean(UserManager.class);
    User user = new User();
    user.setUsername("Tom");
    log.debug("Previous username:" + user.getUsername());
    User userUpdated = userManager.updateUserName(user, "John");
    log.debug("New username:" + userUpdated.getUsername());

    //Using Bean
    MailService mailService = context.getBean(MailService.class);
    //MailService mailService = (MailService)context.getBean("mailService"); //default bean ID same as method name
    boolean result = mailService.sendMessage("You have a new mail");
    log.debug("mail result: " + result);

    //Properties file output
    log.debug("mail.username: " + mailService.getUsername());
    log.debug("mail.password: " + mailService.getPassword());

    //Autowired through constructor
    DatabaseService databaseService = context.getBean(DatabaseService.class);
    Assert.notNull(databaseService);

    LoginService loginService = context.getBean(LoginService.class);
    loginService.getLogService().log("loginService.log() is called");

    RegisterService registerService = context.getBean(RegisterService.class);
    Assert.notNull(registerService);
    registerService.registerUser(user);
    registerService.getLog().log("log interface log() is called");

    try {
      userManager.throwUserUpdateExceptionMethod();
    } catch (Exception e) {
    }
    try {
      userManager.throwNotUserUpdateExceptionMethod();
    } catch (Exception e) {
    }

    userManager.deleteUser(userUpdated);

    ComplexBean complexBean = context.getBean(ComplexBean.class);
    Assert.notNull(complexBean);
    log.debug(complexBean.toString());

    ((ConfigurableApplicationContext) (context)).close();


  }

}
