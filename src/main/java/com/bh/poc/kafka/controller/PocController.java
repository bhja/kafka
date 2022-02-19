package com.bh.poc.kafka.controller;

import com.bh.poc.kafka.model.Request;
import com.bh.poc.kafka.service.KafkaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class PocController {

    private final KafkaService service;

    public PocController(@Qualifier(value = "kafkaService") KafkaService pService){
        service = pService;
    }

    @PostMapping(value="/producer",
            consumes =  MediaType.APPLICATION_JSON_VALUE
            )
    public void send(@RequestBody Request request) {
        service.sendMessage(request);
    }

}
