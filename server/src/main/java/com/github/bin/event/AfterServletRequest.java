package com.github.bin.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.ServletRequestHandledEvent;

/**
 * @author bin
 * @since 2026/02/09
 */
@Slf4j
@Component
public class AfterServletRequest implements ApplicationListener<ServletRequestHandledEvent> {
    @Override
    public void onApplicationEvent(ServletRequestHandledEvent event) {
        var code = event.getStatusCode();
        var method = event.getMethod();
        var processingTimeMillis = event.getProcessingTimeMillis();
        var requestUrl = event.getRequestUrl();
        if (code == 200) {
            log.info("请求完成: {} {} {}ms {}", method, code, processingTimeMillis, requestUrl);
        } else {
            log.warn("请求异常: {} {} {}ms {}", method, code, processingTimeMillis, requestUrl);
        }
    }

}
