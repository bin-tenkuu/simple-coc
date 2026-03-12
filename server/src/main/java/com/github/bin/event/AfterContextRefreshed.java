package com.github.bin.event;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @author bin
 * @since 2025/12/18
 */
@Slf4j
@Component
public class AfterContextRefreshed implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
        System.out.println("启动成功");
    }

}
