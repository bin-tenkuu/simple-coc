package com.github.bin.config;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.strategy.LoadBalanceDynamicDataSourceStrategy;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import lombok.val;
import org.sqlite.SQLiteDataSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * @author bin
 * @since 2023/08/22
 */
public class MsgDataSource {
    public static final DynamicRoutingDataSource DATA_SOURCE;

    static {
        DATA_SOURCE = new DynamicRoutingDataSource();
        DATA_SOURCE.setPrimary("default");
        DATA_SOURCE.setStrict(true);
        DATA_SOURCE.setStrategy(LoadBalanceDynamicDataSourceStrategy.class);
        addDataSource("default");
    }

    public static void addDataSource(String name) {
        val url = "sql/hisMsg_" + name + ".db";
        val dbFile = new File(url);
        if (!dbFile.exists()) {
            try {
                Files.copy(Path.of("sql/hisMsg.db"), dbFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        val sqLiteDataSource = new SQLiteDataSource();
        sqLiteDataSource.setUrl("jdbc:sqlite:" + url);
        sqLiteDataSource.setSharedCache(true);
        sqLiteDataSource.setLoadExtension(true);
        sqLiteDataSource.setCountChanges(true);
        sqLiteDataSource.setLegacyFileFormat(false);
        sqLiteDataSource.setLegacyAlterTable(false);
        DATA_SOURCE.addDataSource(name, sqLiteDataSource);
    }

    public static void setDataSource(String name) {
        if (!DATA_SOURCE.getDataSources().containsKey(name)) {
            addDataSource(name);
        }
        DynamicDataSourceContextHolder.push(name);
    }

    public static void removeDataSource(String name) {
        DATA_SOURCE.removeDataSource(name);
    }

    public static void set(String name) {
        setDataSource(name);
    }
}
