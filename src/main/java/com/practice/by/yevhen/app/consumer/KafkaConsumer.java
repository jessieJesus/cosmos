package com.practice.by.yevhen.app.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

//@Component
public class KafkaConsumer {

    private final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
    private String payload;

//    @Bean
    public Consumer<String> test() {
        return message -> {
            logger.info(message);
            payload = message;
        };
    }

    public String getPayload() {
        return payload;
    }

}
