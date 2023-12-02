package com.github.npawlenko.evotingapp.configuration;

import java.util.Properties;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import io.github.cdimascio.dotenv.Dotenv;

@Configuration
@EnableAutoConfiguration(exclude = MailSenderAutoConfiguration.class)
public class EmailConfiguration {

	@Bean
	public JavaMailSender javaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		Dotenv dotenv = Dotenv.load();

		mailSender.setHost(dotenv.get("SPRING_MAIL_HOST"));
		mailSender.setPort(Integer.parseInt(dotenv.get("SPRING_MAIL_PORT")));
		mailSender.setUsername(dotenv.get("SPRING_MAIL_USERNAME"));
		mailSender.setPassword(dotenv.get("SPRING_MAIL_PASSWORD"));

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");

		return mailSender;
	}
}
