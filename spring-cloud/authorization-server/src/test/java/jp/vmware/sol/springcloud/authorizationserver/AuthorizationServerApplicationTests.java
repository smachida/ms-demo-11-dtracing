package jp.vmware.sol.springcloud.authorizationserver;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"eureka.client.enabled=false","spring.cloud.config.enabled=false"})
@AutoConfigureMockMvc
class AuthorizationServerApplicationTests {
    @Autowired
    MockMvc mvc;

    @Test
    void contextLoads() {
    }

}
