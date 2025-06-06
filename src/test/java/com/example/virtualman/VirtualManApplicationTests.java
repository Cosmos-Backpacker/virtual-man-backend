package com.example.virtualman;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VirtualManApplicationTests {
    VirtualManApplication virtualManApplication = new VirtualManApplication();

    @Test
    void contextLoads() {
    }

    @Test
    void testGetRes() {
        virtualManApplication.getRes();
    }


    @Test
    void testReq() {
        virtualManApplication.req();
    }
}
