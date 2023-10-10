package com.github.bin.controller;

import com.github.bin.model.MessageOut;
import com.github.bin.model.ResultModel;
import com.github.bin.service.HisMsgService;
import com.github.bin.util.MessageUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

/**
 * @author bin
 * @since 2023/09/22
 */
@Tag(name = "chat")
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    @GetMapping("/historyMsg")
    public ResultModel<List<MessageOut.Msg>> historyMsg(
            @RequestParam String roomId,
            @RequestParam(required = false) Long msgId
    ) {
        val list = HisMsgService.historyMsg(roomId, msgId, 20)
                .stream()
                .map(MessageUtil::toMessage)
                .toList();
        return ResultModel.success(list);
    }

    @GetMapping("/sseRoom")
    public SseEmitter sseRoom() {
        log.info("sseRoom");
        SseEmitter emitter = new SseEmitter();
        new SseEmitterThread(emitter).start();
        return emitter;
    }

    @RequiredArgsConstructor
    private static class SseEmitterThread extends Thread {
        private final SseEmitter emitter;
        private volatile transient boolean running = true;

        @Override
        public void run() {
            emitter.onCompletion(this::interrupt);
            emitter.onError((e) -> interrupt());
            emitter.onTimeout(this::interrupt);
            int i = 0;
            while (running && !interrupted()) {
                // a=new EventSource("http://localhost:8080/api/sseRoom");
                // a.addEventListener('name',event=>console.log(event))
                val event = SseEmitter.event()
                        .id(String.valueOf(i++))
                        .name("name")
                        .data("{ \"message\": \"hello\"}\n\n", MediaType.APPLICATION_JSON);
                try {
                    if (running) {
                        emitter.send(event);
                    }
                } catch (IOException e) {
                    this.running = false;
                    break;
                }
            }
        }

        @Override
        public void interrupt() {
            this.running = false;
            super.interrupt();
        }
    }
}
