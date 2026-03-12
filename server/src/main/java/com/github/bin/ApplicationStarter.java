package com.github.bin;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.val;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

/**
 * @author bin
 * @since 2023/08/22
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Demo 接口",
                description = "Demo 相关 API",
                version = "v1.0"
        )
)
@SpringBootApplication(
        exclude = {
                UserDetailsServiceAutoConfiguration.class
        }
)
@EnableWebSocket
@EnableScheduling
public class ApplicationStarter {
    public static void main(String[] args) {
        val application = new SpringApplication(ApplicationStarter.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }
}
