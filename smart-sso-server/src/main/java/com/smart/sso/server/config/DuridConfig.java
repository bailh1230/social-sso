package com.smart.sso.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/***
* @description
* @author        bailihong
* @createdate    2020/11/29 22:41
* @version       v1.0
*/
@Data
@Component
@ConfigurationProperties(prefix = "spring.datasource.druid")
public class DuridConfig {
    int initialSize = 10;
    int maxActive = 100;
    int minIdle = 5;
    int maxWait = 60000;
    String validationQuery = "select 1";
    boolean testOnBorrow = true;
    boolean testWhileIdle = true;
}
