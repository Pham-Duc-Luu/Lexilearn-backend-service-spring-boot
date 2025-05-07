package MainBackendService.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import java.util.Base64;
import java.util.Properties;

@Configuration
public class EmailConfig {
    Logger logger = LogManager.getLogger(EmailConfig.class);

    @Value("${mail.host}")
    private String mailServerHost;

    @Value("${mail.port}")
    private String mailServerPort;

    @Value("${mail.username}")
    private String mailServerUsername;

    @Value("${mail.password}")
    private String mailServerPassword;


    @Bean
    public JavaMailSender getJavaMailSender() {
        String password = new String(Base64.getDecoder().decode(mailServerPassword));
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(mailServerHost);
        mailSender.setPort(Integer.parseInt(mailServerPort));
        mailSender.setUsername(mailServerUsername);
        mailSender.setPassword(password);
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
    
        return mailSender;
    }

}
