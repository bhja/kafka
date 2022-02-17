package com.bh.poc.kafka.model;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class Request {
    int limit = 100000;
    boolean logIt;
    String correlationId= UUID.randomUUID().toString();
}
