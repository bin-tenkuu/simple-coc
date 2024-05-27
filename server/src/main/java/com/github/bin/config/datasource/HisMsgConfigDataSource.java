package com.github.bin.config.datasource;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.github.bin.service.HisMsgService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

/**
 * @author bin
 * @since 2023/08/22
 */
@Component
@EnableConfigurationProperties(MybatisPlusProperties.class)
@MapperScan(
        basePackages = "com.github.bin.mapper.msg",
        sqlSessionTemplateRef = "msgSqlSessionTemplate"
)
@RequiredArgsConstructor
public class HisMsgConfigDataSource {
    private final MybatisPlusProperties properties;

    private void modify(MybatisSqlSessionFactoryBean bean) {
        val globalConfig = new GlobalConfig();
        BeanUtils.copyProperties(properties.getGlobalConfig(), globalConfig);
        bean.setGlobalConfig(globalConfig);
        val configuration = new MybatisConfiguration();
        BeanUtils.copyProperties(properties.getConfiguration(), configuration);
        bean.setConfiguration(configuration);
    }

    @Bean("msgSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        val bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(HisMsgService.DATA_SOURCE);
        val resolver = new PathMatchingResourcePatternResolver();
        bean.setMapperLocations(resolver.getResources("classpath*:mapper/msg/*.xml"));
        modify(bean);
        val interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.POSTGRE_SQL));
        bean.setPlugins(interceptor);
        return bean.getObject();
    }

    @Bean("msgSqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(
            @Qualifier("msgSqlSessionFactory") SqlSessionFactory sqlSessionFactory
    ) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
