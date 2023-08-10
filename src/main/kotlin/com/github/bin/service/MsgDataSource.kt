package com.github.bin.service

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource
import com.baomidou.dynamic.datasource.strategy.LoadBalanceDynamicDataSourceStrategy
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder
import org.sqlite.SQLiteDataSource
import java.io.File

/**
 * @author bin
 * @since 2023/08/10
 */
object MsgDataSource {
    val dataSource = DynamicRoutingDataSource().apply {
        setPrimary("default")
        setStrict(true)
        setStrategy(LoadBalanceDynamicDataSourceStrategy::class.java)
    }

    init {
        addDataSource("default")
    }

    fun addDataSource(name: String) {
        val url = "sql/hisMsg_$name.db"
        val dbFile = File(url)
        if (!dbFile.exists()) {
            File("sql/hisMsg.db").copyTo(dbFile)
        }
        val sqLiteDataSource = SQLiteDataSource().apply {
            setUrl("jdbc:sqlite:$url")
            setSharedCache(true)
            setLoadExtension(true)
            setCountChanges(true)
            setLegacyFileFormat(false)
            setLegacyAlterTable(false)
        }
        dataSource.addDataSource(name, sqLiteDataSource)
    }

    fun setDataSource(name: String) {
        if (name !in dataSource.dataSources) addDataSource(name)
        DynamicDataSourceContextHolder.push(name)
    }

    fun removeDataSource(name: String) {
        dataSource.removeDataSource(name)
    }
}
