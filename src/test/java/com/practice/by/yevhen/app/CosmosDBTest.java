package com.practice.by.yevhen.app;

import com.practice.by.yevhen.app.dto.User;
import com.practice.by.yevhen.app.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.CosmosDBEmulatorContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CosmosDBTest {

    @Autowired
    private UserRepository userRepository;

    @ClassRule
    public static CosmosDBEmulatorContainer emulator = new CosmosDBEmulatorContainer(
            DockerImageName.parse("mcr.microsoft.com/cosmosdb/linux/azure-cosmos-emulator"));

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        String uri = "mongodb://" + emulator.getContainerIpAddress() + ":" + emulator.getFirstMappedPort() + "/test";
        registry.add("spring.data.mongodb.uri", () -> uri);
    }

    @Before
    public void setUp() throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException {
        var keyStoreFile = tempFolder.newFile("azure-cosmos-emulator.keystore").toPath();
        var keyStore = emulator.buildNewKeyStore();
        keyStore.store(new FileOutputStream(keyStoreFile.toFile()), emulator.getEmulatorKey().toCharArray());
        System.setProperty("javax.net.ssl.trustStore", keyStoreFile.toString());
        System.setProperty("javax.net.ssl.trustStorePassword", emulator.getEmulatorKey());
        System.setProperty("javax.net.ssl.trustStoreType", "PKCS12");
    }
    
    @After
    public void tearDown() {
        System.clearProperty("javax.net.ssl.trustStore");
        System.clearProperty("javax.net.ssl.trustStorePassword");
        System.clearProperty("javax.net.ssl.trustStoreType");
    }

    @Test
    public void shouldVerifySuccessfullyConnectionToTheCosmosDB() {
        var id = "someId";
        var userName = "Petro";
        var user = User.builder()
                .id(id)
                .name(userName)
                .build();

        userRepository.save(user);
        var foundedUser = userRepository.findById(id).get();

        assertThat(userName).isEqualTo(foundedUser.getName());
    }

}
