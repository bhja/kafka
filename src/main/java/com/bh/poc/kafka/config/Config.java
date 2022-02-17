package com.bh.poc.kafka.config;

import com.bh.poc.kafka.model.Message;
import com.bh.poc.kafka.service.KafkaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.kafka.sender.SenderOptions;

import java.util.Map;

@Configuration
public class Config {


    @Bean(name = "reactiveProducer")
    public ReactiveKafkaProducerTemplate<String, Message> reactiveKafkaProducerTemplate(
            KafkaProperties properties) {
        Map<String, Object> props = properties.buildProducerProperties();
        return new ReactiveKafkaProducerTemplate<>(SenderOptions.create(props));
    }

    @Bean(name="kafkaService")
    public KafkaService kafkaService(@Qualifier(value = "reactiveProducer") ReactiveKafkaProducerTemplate<String, Message> template,
            ObjectMapper mapper){
        return new KafkaService(template,mapper);
    }




}
