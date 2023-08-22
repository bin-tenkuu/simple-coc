package com.github.bin.config

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource
import com.baomidou.mybatisplus.annotation.DbType
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties
import com.baomidou.mybatisplus.core.MybatisConfiguration
import com.baomidou.mybatisplus.core.config.GlobalConfig
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean
import org.apache.ibatis.session.SqlSessionFactory
import org.mybatis.spring.SqlSessionTemplate
import org.mybatis.spring.annotation.MapperScan
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.stereotype.Component
import javax.sql.DataSource


/**
 *  @Date:2023/4/9
 *  @author bin
 *  @version 1.0.0
 */
@Component
@EnableConfigurationProperties(value = [MybatisPlusProperties::class])
@MapperScan(
    basePackages = ["com.github.bin.mapper.msg"],
    sqlSessionTemplateRef = "msgSqlSessionTemplate"
)
class DateSourceHisMsgConfig(
    private val properties: MybatisPlusProperties
) {

    private fun modify(bean: MybatisSqlSessionFactoryBean) {
        val globalConfig = GlobalConfig()
        BeanUtils.copyProperties(properties.globalConfig, globalConfig)
        bean.setGlobalConfig(globalConfig)
        val configuration = MybatisConfiguration()
        BeanUtils.copyProperties(properties.configuration, configuration)
        bean.configuration = configuration
    }

    @Bean(name = ["msgDataSource"])
    fun dataSource(
        properties: DataSourceProperties
    ): DynamicRoutingDataSource {
        return MsgDataSource.DATA_SOURCE
    }

    @Bean(name = ["msgSqlSessionFactory"])
    @Throws(Exception::class)
    fun sqlSessionFactory(
        @Qualifier("msgDataSource") dataSource: DataSource
    ): SqlSessionFactory {
        val bean = MybatisSqlSessionFactoryBean()
        bean.setDataSource(dataSource)
        val resolver = PathMatchingResourcePatternResolver()
        bean.setMapperLocations(*resolver.getResources("classpath*:mapper/msg/*.xml"))
        modify(bean)
        val interceptor = MybatisPlusInterceptor()
        interceptor.addInnerInterceptor(PaginationInnerInterceptor(DbType.POSTGRE_SQL))
        bean.setPlugins(interceptor)
        return bean.getObject()!!
    }

    @Bean(name = ["msgSqlSessionTemplate"])
    fun sqlSessionTemplate(
        @Qualifier("msgSqlSessionFactory") sqlSessionFactory: SqlSessionFactory
    ): SqlSessionTemplate {
        return SqlSessionTemplate(sqlSessionFactory)
    }

}
