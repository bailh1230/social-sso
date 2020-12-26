package com.smart.sso.server.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/***
* @description
* @author        bailihong
* @createdate    2020/11/29 23:06
* @version       v1.0
*/
@Configuration
@tk.mybatis.spring.annotation.MapperScan(basePackages = MasterDataSourceConfig.PACKAGE, sqlSessionTemplateRef = "masterSqlSessionTemplate")
public class MasterDataSourceConfig {
    static final String PACKAGE = "com.smart.sso.server.mapper";
    private static final String MAPPER_LOCATION = "classpath:mapper/*.xml";

    @Autowired
    DuridConfig duridConfig;

    @Autowired
    MysqlConfig mysqlConfig;

    @Bean(name = "masterDataSource")
    @Primary
    public DataSource masterDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(mysqlConfig.getDriverClassName());
        dataSource.setUrl(mysqlConfig.getUrl());
        dataSource.setUsername(mysqlConfig.getUsername());
        dataSource.setPassword(mysqlConfig.getPassword());
        dataSource.setInitialSize(duridConfig.getInitialSize());
        dataSource.setMaxActive(duridConfig.getMaxActive());
        dataSource.setMinIdle(duridConfig.getMinIdle());
        dataSource.setMaxWait(duridConfig.getMaxWait());
        dataSource.setValidationQuery(duridConfig.getValidationQuery());
        dataSource.setTestOnBorrow(duridConfig.isTestOnBorrow());
        dataSource.setTestWhileIdle(duridConfig.isTestWhileIdle());
        return dataSource;
    }

    @Bean(name = "masterTransactionManager")
    @Primary
    public DataSourceTransactionManager masterTransactionManager() {
        return new DataSourceTransactionManager(masterDataSource());
    }

    @Bean(name = "masterSqlSessionFactory")
    @Primary
    public SqlSessionFactory masterSqlSessionFactory(@Qualifier("masterDataSource") DataSource masterDataSource)
            throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(masterDataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(MasterDataSourceConfig.MAPPER_LOCATION));
        return sessionFactory.getObject();
    }

    @Bean(name = "masterSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate setSqlSessionTemplate(@Qualifier("masterSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
