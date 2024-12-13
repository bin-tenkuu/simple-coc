package com.github.bin.config.datasource;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
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

    private void modify(MybatisSqlSessionFactoryBean bean) {
        val globalConfig = new GlobalConfig();
        BeanUtils.copyProperties(properties.getGlobalConfig(), globalConfig);
        bean.setGlobalConfig(globalConfig);
        val configuration = new MybatisConfiguration();
        BeanUtils.copyProperties(properties.getConfiguration(), configuration);
        bean.setConfiguration(configuration);
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
        val resolver = new PathMatchingResourcePatternResolver();
        bean.setMapperLocations(resolver.getResources("classpath*:mapper/master/*.xml"));
        modify(bean);
        return bean.getObject();
    }

    @Bean("masterSqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(
            @Qualifier("masterSqlSessionFactory") SqlSessionFactory sqlSessionFactory
    ) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
