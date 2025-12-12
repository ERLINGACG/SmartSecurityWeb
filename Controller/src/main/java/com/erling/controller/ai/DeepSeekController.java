package com.erling.controller.ai;

import com.erling.service.ai.DeepSeekService;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("ai/deepseek")
public class DeepSeekController {

    private final DeepSeekService  deepSeekService;

    public DeepSeekController(DeepSeekService deepSeekService) {
        this.deepSeekService = deepSeekService;
    }


    @GetMapping(value = "/chat/generateReport")
    public Flux<ServerSentEvent<String>> getChatHistory( @RequestParam String topic,
                                                         @RequestParam Long start,
                                                         @RequestParam Long end) {
        return deepSeekService.getChatHistory(topic, start, end);
    }



}
