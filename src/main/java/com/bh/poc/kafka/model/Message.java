package com.bh.poc.kafka.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Message {
    private String id;
    private String forewordKafka =
            "\n \n \nWhile the publisher and the authors have used good faith efforts to ensure that the information and "
            + "instructions contained in this work are accurate, the publisher and the authors disclaim all responsibility "
            + "for errors or omissions,including without limitation responsibility for damages resulting from the use of "
            + "or reliance on this work.Use of the information and instructions contained in this work is at your own"
            + "risk.If any code samples or other technology this work contains or describes is subject to open source "
            + "licenses or the intellectual property rights of others, it is your responsibility to ensure that your use "
            + "thereof complies with such licenses and/or rights.\n \n \n \n  ";
    private String uuid;
    private long creationTime = System.currentTimeMillis();

}
