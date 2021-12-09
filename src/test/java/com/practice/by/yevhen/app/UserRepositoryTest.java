package com.practice.by.yevhen.app;

import com.practice.by.yevhen.app.config.MongoTestConfig;
import com.practice.by.yevhen.app.dto.User;
import com.practice.by.yevhen.app.repository.UserRepository;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = MongoTestConfig.class)
public class UserRepositoryTest {

    @ClassRule
    public static final MongoDBContainer MONGO_DB_CONTAINER = new MongoDBContainer(DockerImageName.parse("mongo:4.4.2"));

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO_DB_CONTAINER::getReplicaSetUrl);
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldVerifyThatUserWasCreated() {
        var someId = "someId";
        var userName = "Petro";
        var user = User.builder()
                .name(userName)
                .id(someId)
                .build();

        userRepository.save(user);
        var foundedUsers = userRepository.findById(someId);

        assertThat(foundedUsers).isPresent();
        assertThat(someId).isEqualTo(foundedUsers.get().getId());
    }

}
