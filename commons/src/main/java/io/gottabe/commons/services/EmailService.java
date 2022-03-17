package io.gottabe.commons.services;

import io.gottabe.commons.config.SiteConfig;
import io.gottabe.commons.vo.UserVO;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    @Autowired
    private TemplateService templateService;

    @Autowired
    private SiteConfig siteConfig;

    @Autowired
    private LocaleResolver localeResolver;

    @Autowired
    private HttpServletRequest request;

    @Value("${gottabeio.config.mail.smtpHost}")
    private String smtpHost;

    @Value("${gottabeio.config.mail.smtpPort}")
    private Integer smtpPort;

    @Value("${gottabeio.config.mail.fromMail}")
    private String fromEmail;

    @Value("${gottabeio.config.mail.user}")
    private String mailUser;

    @Value("${gottabeio.config.mail.pass}")
    private String mailPass;

    public void sendMailUser(UserVO user, String subject, String template) throws Exception {
        Map<String, Object> root = createRoot(user);
        String htmlMsg = templateService.processTemplate(template, TemplateService.TemplateType.MAIL, localeResolver.resolveLocale(request), root);
        sendMail(user.getEmail(), subject, htmlMsg);
    }

    /**
     * A good approach would be to send it to a JMS queue or Kafka topic, consume it and send it. But today we are only going to send it directly.
     * @param recipientEmail the recipient email
     * @param subject the mail subject
     * @param htmlMsg the html message
     * @throws EmailException if something bad happens
     */
    private void sendMail(String recipientEmail, String subject, String htmlMsg) throws EmailException {
        HtmlEmail email = new HtmlEmail();
        email.setHostName(smtpHost);
        email.setSmtpPort(smtpPort);
        email.setAuthenticator(new DefaultAuthenticator(mailUser, mailPass));
        email.setStartTLSEnabled(true);
        email.setFrom(fromEmail);
        email.setSubject(subject);
        email.setHtmlMsg(htmlMsg);
        email.addTo(recipientEmail);
        email.send();
    }

    private Map<String, Object> createRoot(UserVO user) {
        Map<String, Object> root = new HashMap<>();
        root.put("config", siteConfig);
        root.put("user", user);
        return root;
    }

}
