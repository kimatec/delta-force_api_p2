package com.revature.deltaforce.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("com.revature.deltaforce")
@PropertySource("classpath:application.properties")
@Import({WebConfig.class})
public class AppConfig {
}
