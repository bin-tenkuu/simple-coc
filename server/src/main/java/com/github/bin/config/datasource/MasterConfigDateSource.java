package com.github.bin.config.datasource;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

/**
 * @author bin
 * @since 2023/08/22
 */
@Component
@EnableConfigurationProperties(MybatisPlusProperties.class)
@MapperScan(
        basePackages = "com.github.bin.mapper.master",
        sqlSessionTemplateRef = "masterSqlSessionTemplate"
)
@RequiredArgsConstructor
public class MasterConfigDateSource {
    private final MybatisPlusProperties properties;

    public void modify(MybatisSqlSessionFactoryBean factory, String[] mapperLocations) {
        factory.setGlobalConfig(properties.getGlobalConfig());
        val configuration = new MybatisConfiguration();
        val coreConfiguration = properties.getConfiguration();
        properties.setMapperLocations(mapperLocations);
        factory.setMapperLocations(properties.resolveMapperLocations());
        if (coreConfiguration != null) {
            coreConfiguration.applyTo(configuration);
        }
        factory.setConfiguration(configuration);
    }

    @Bean("masterDataSource")
    public HikariDataSource dataSource(
            DataSourceProperties properties
    ) {
        val dataSource = properties
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        if (StringUtils.hasText(properties.getName())) {
            dataSource.setPoolName(properties.getName());
        }
        return dataSource;
    }

    @Bean("masterSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(
            @Qualifier("masterDataSource") DataSource dataSource
    ) throws Exception {
        val bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        modify(bean, new String[]{"classpath*:mapper/master/*.xml"});
        return bean.getObject();
    }

    @Bean("masterSqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(
            @Qualifier("masterSqlSessionFactory") SqlSessionFactory sqlSessionFactory
    ) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
