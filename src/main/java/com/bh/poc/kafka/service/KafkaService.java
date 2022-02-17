package com.bh.poc.kafka.service;

import com.bh.poc.kafka.model.Message;
import com.bh.poc.kafka.model.Request;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.scheduling.annotation.Async;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class KafkaService {

    private ReactiveKafkaProducerTemplate<String,Message> template;
    private ObjectMapper mapper;



    public KafkaService(ReactiveKafkaProducerTemplate<String,Message> pTemplate,ObjectMapper pMapper){
        template = pTemplate;
        mapper = pMapper;
    }

    protected ReactiveKafkaProducerTemplate<String,Message> getTemplate(){
        return template;
    }

    protected ObjectMapper getMapper(){
        return mapper;
    }
    @Async
    public void sendMessage(Request input) {
        try {
            AtomicInteger count = new AtomicInteger(0);
            AtomicLong timer = new AtomicLong(System.currentTimeMillis());
            int i = 1;
            do {
                int rand = ThreadLocalRandom.current().nextInt(1,10);
                Message message  = new Message();
                message.setId(String.valueOf(i));
                message.setUuid(UUID.randomUUID().toString());
                log.debug("Size of message {}",getMapper().writeValueAsBytes(message).length);

                ProducerRecord<String,Message> record = new ProducerRecord<>("poc-"+rand,message.getId(),message);
                Mono<SenderResult<String>> request = getTemplate().send(SenderRecord.create(record,message.getId()))
                .doOnError((e) ->
                    log.error("Could not process {} ", e.getMessage())
                ).doOnRequest((v)->{
                    log.debug("Record {}",message.getId());
                    count.incrementAndGet();
                if(((System.currentTimeMillis() - timer.get())/1000)% 60 == 30){
                    log.info("Total number of records processed in last 30 seconds {} ",count.get());
                    timer.set(System.currentTimeMillis());
                }})
                .subscribeOn(Schedulers.parallel());
                if(input.isLogIt()){
                    request = request.log();
                }
                request.subscribe();
                i = i+1;
            }while (input.getLimit() >= i);

        }catch (Exception e){
            log.error("Could not send message {} ",e.getMessage()== null ? e.getCause():e.getMessage());
        }
    }



}
