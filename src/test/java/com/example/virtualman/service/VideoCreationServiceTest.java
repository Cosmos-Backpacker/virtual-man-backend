package com.example.virtualman.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.*;


@SpringBootTest
class VideoCreationServiceTest {

    @Autowired
    private VideoCreationService videoCreationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateVideo() throws Exception {

        String result = videoCreationService.createVideo("virtualmanKey", "ssmlText", 0f);
        Assertions.assertEquals("replaceMeWithExpectedResult", result);
    }

    @Test
    void testCreateDefaultVideo() throws Exception {

        String result = videoCreationService.createDefaultVideo();
        Assertions.assertNotNull(result);
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme