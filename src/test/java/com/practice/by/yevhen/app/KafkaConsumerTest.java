package com.practice.by.yevhen.app;

import com.practice.by.yevhen.app.consumer.KafkaConsumer;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles(value = "test")
public class KafkaConsumerTest {

    @ClassRule
    public static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.0.0"));

    @Autowired
    private StreamBridge bridge;

    @Autowired
    private KafkaConsumer consumer;

    private static final String BINDING_NAME = "test-out-0";

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.stream.kafka.binder.brokers", kafka::getBootstrapServers);
    }

    @Test
    public void shouldVerifyConsumerGetOneMessageSentFromProducer() {
        //given
        var message = "Test message of integration test";

        //when
        bridge.send(BINDING_NAME, new GenericMessage<>(message.getBytes()));

        //then
        await().atMost(Duration.ofSeconds(7)).until(() -> message.equals(consumer.getPayload()));
        assertThat(message).isEqualTo(consumer.getPayload());
    }
}
