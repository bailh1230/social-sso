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
@ConfigurationProperties(prefix = "spring.datasource.mysql")
public class MysqlConfig {
    String url = "jdbc:mysql://localhost:3306/datalake_test?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true";
    String driverClassName = "com.mysql.jdbc.Driver";
    String username = "root";
    String password = "123456";
}
