package com.erling.service.ai;

import com.erling.service.obj.ServiceObject;
import com.erling.service.redis.ser.RedisZSetService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class DeepSeekService extends ServiceObject {
    private final ChatClient chatClient;

    private final RedisZSetService redisZSetService;

    public DeepSeekService(ChatClient chatClient, RedisZSetService redisZSetService) {
        this.chatClient = chatClient;
        this.redisZSetService = redisZSetService;
    }


    public Flux<ServerSentEvent<String>> getChatHistory( String topic,
                                                          Long start,
                                                          Long end
    ) {
        this.log.info("获取：{}设备，时间范围内的记录，开始时间：{}，结束时间：{}",topic,start,end);
        String log= redisZSetService.getReverseRangeByTimestamp(topic,start,end).toString();
        return getServerSentEventFlux("请帮我分析一下这个日志，什么时候出现了人，出现了多少人"+log);
    }

    private Flux<ServerSentEvent<String>> getServerSentEventFlux(String prompt) {
        return chatClient.prompt(prompt)
                .stream()
                .content()
                .map(content -> ServerSentEvent.builder(content)
                        .event("message")
                        .build())
                .concatWith(Flux.just(ServerSentEvent.builder("[STREAM_END]")
                        .event("message")
                        .build()))
                .onErrorResume(e -> {
                    log.error("DeepSeek API调用异常", e);
                    return Flux.just(
                            ServerSentEvent.builder("服务异常: " + e.getMessage())
                                    .event("error")
                                    .build()
                    );
                });
        // 移除 doOnSubscribe 中的 HttpHeaders 设置
    }

}
