package com.proitc.container.dependency.ioc.config.xml;

import com.proitc.bean.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Assert;

/*
 • What is a prefix? 
 
 	Prefixes are used in resource strings for convenience
 	Some example are; 
 	classpath: (used for loading file from classpath) classpath:util.properties
 	file: (load as URL from the filesystem) file:///resource/path/file.txt
 	http: (load as URL) http://tunatore.com/resource/path/file.txt 	
 	
 	sample Resource template = ctx.getResource("classpath:util.properties");
    ApplicationContext ctx = new FileSystemXmlApplicationContext("conf/application-context.xml");
    
 */

public class MainApp {

  private static final Logger log = LoggerFactory.getLogger(MainApp.class);

  public static void main(String[] args) {

    // Creating the application context
    ApplicationContext context = new ClassPathXmlApplicationContext("application-context.xml");
	    
		/* XML level profile or Class level @Profile configuration
		((ConfigurableApplicationContext)(context)).getEnvironment().setActiveProfiles("production");
		((ConfigurableApplicationContext)(context)).refresh();
		*/

    //no bean id required if the type of requested object is unique
    //hence context does not know the type casting is required with the following approach
    UserManager userManager = (UserManager) context.getBean("userManager");

    User user = new User();
    user.setUsername("Tom");
    log.info("Previous username:" + user.getUsername());

    User userUpdated = userManager.updateUserName(user, "John");
    log.info("New username:" + userUpdated.getUsername());

    //no bean id required if the type of requested object is unique
    //no casting is required
    MailService mailService = context.getBean(MailService.class);
    log.info("Inner bean state in mailService: " + mailService
      .getServiceConfig()
      .getMaxHourlyEmailLimit());
    boolean result = mailService.sendMessage("You have a new mail");
    log.info("mail result: " + result);

    //Properties file output
    log.info("mail.username: " + mailService.getUsername());
    log.info("mail.password: " + mailService.getPassword());

    //no casting is required with the following approach
    DatabaseService databaseService = context.getBean("databaseService", DatabaseService.class);
    Assert.notNull(databaseService);

    LoginService loginService = context.getBean(LoginService.class);
    loginService
      .getLogService()
      .log("loginService.log() is called");

    RegisterService registerService = context.getBean(RegisterService.class);
    Assert.notNull(registerService);
    registerService.registerUser(user);
    registerService
      .getLog()
      .log("log interface log() is called");

    try {
      userManager.throwUserUpdateExceptionMethod();
    } catch (Exception e) {
    }
    try {
      userManager.throwNotUserUpdateExceptionMethod();
    } catch (Exception e) {
    }

    userManager.deleteUser(userUpdated);

    // Bean inheritance example
    User testUser = context.getBean("testUser", User.class);
    log.info("Bean inheritance testUser username: " + testUser.getUsername());
    log.info("Bean inheritance testUser password: " + testUser.getPassword());
    log.info("Bean inheritance testUser active: " + testUser.isActive());

    SuperUser superUser = context.getBean("superUser", SuperUser.class);
    log.info("Bean inheritance superUser systemPassword: " + superUser.getSystemPassword());
    log.info("Bean inheritance superUser contact: " + superUser.getContact());

    ComplexBean complexBean = context.getBean(ComplexBean.class);
    Assert.notNull(complexBean);
    log.info(complexBean.toString());

    //close the application and release all sources and locks
    ((ConfigurableApplicationContext) (context)).close();

    // or Register a shutdown hook with the JVM runtime, will close the context on JVM shutdown
    //((ConfigurableApplicationContext)(context)).registerShutdownHook();

  }

}
