package com.bh.poc.kafka.model;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class Request {
    private int limit = 1000000;
    private boolean logIt;
    String type = "single";
    private int maxSeconds = 30;
    private String correlationId= UUID.randomUUID().toString();
}
