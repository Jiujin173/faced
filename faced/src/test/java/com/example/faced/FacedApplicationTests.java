package com.example.faced;

import ch.ethz.ssh2.Connection;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class FacedApplicationTests {

    @Test
    void contextLoads() throws IOException {
        Connection connection = new Connection("192.168.107.128",8888);
        connection.connect();
    }

}
