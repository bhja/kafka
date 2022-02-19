package com.bh.poc.kafka.service;

import com.bh.poc.kafka.model.Message;
import com.bh.poc.kafka.model.Request;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.scheduling.annotation.Async;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
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
            runTimedJob(input);
    }

    protected void runTimedJob(Request input){
        try {
            AtomicLong completedTimer = new AtomicLong(-1);
            AtomicInteger counter = new AtomicInteger(0);
            AtomicInteger completed = new AtomicInteger(0);
            AtomicBoolean lock = new AtomicBoolean(true);
            long start = System.currentTimeMillis();
            Scheduler scheduler  = Schedulers.single() ;
            do {
                int rand = ThreadLocalRandom.current().nextInt(1,10);
                switch (input.getType()){
                    case "parallel":
                        scheduler= Schedulers.parallel();
                        break;
                    case "bounded":
                        scheduler = Schedulers.boundedElastic();
                        break;

                }
                Message message  = new Message();
                message.setId(String.valueOf(counter.incrementAndGet()));
                message.setUuid(UUID.randomUUID().toString());
                log.debug("Size of message {}",getMapper().writeValueAsBytes(message).length);

                ProducerRecord<String,Message> record = new ProducerRecord<>("poc-"+rand,message.getId(),message);
                Mono<SenderResult<String>> request = getTemplate().send(SenderRecord.create(record,message.getId()))
                        .doOnError((e) ->
                                log.error("Could not process {} ", e.getMessage())
                        );
                request.subscribeOn(scheduler);
                if(input.isLogIt()){
                    request = request.log();
                }
                request.doOnSuccess((val)->{

                    completed.incrementAndGet();

                    boolean check =jobTime(completedTimer,completed,counter,input.getMaxSeconds(),lock.get());

                    synchronized (val) {
                    if(check && lock.get()) {
                        lock.set(false);
                        log.info("Total number of jobs submitted {} in 30+ seconds for request [{}] ", counter.get(),input);
                    }
                    if (completed.get() == counter.get()) {
                        log.info("[{}] completed in total time {} sec", completed.get(),
                                TimeUnit.MILLISECONDS.toSeconds(Instant.now().toEpochMilli() - completedTimer.get()));
                    }
                }
                }).subscribe();
           }while (lock.get());
            log.info("Exiting the submission loop after {} ms" ,System.currentTimeMillis()-start );
        }catch (Exception e){
            log.error("Could not send message {} ",e.getMessage()== null ? e.getCause():e.getMessage());
        }
    }

    protected void initializeTimer(AtomicLong timer){
        if(timer.get()< 0){
            timer.set(Instant.now().toEpochMilli());
        }
    }
    protected boolean jobTime(AtomicLong timer,AtomicInteger jobCounter,AtomicInteger total,int maxSeconds,boolean check){
        initializeTimer(timer);
        long milliseconds = Instant.now().toEpochMilli() - timer.get();
        long seconds =TimeUnit.MILLISECONDS.toSeconds(milliseconds);
        //Anything that is processed in 30-31 seconds.
        if(seconds> 30 && seconds < 31) {
            log.info("Time elapsed {} in ms ,{} seconds,  no of records total:{}, completed :{},pending : {} ", milliseconds , seconds, total.get(),
                    jobCounter.get(), total.get() - jobCounter.get());
        }
        if((seconds == maxSeconds || maxSeconds<=seconds && seconds<=maxSeconds+5) && check){
            {
                log.info("******************Record count in {} seconds  is  total {}, completed {} ***********************",seconds,total.get(),jobCounter.get());
                return true;
            }
        }
        return false;
    }

}
