package com.example.demo;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RpcClientController {

    private static final Logger logger = LoggerFactory.getLogger(RpcClientController.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/send")
    public String send(String message) {

        Message newMessage = MessageBuilder.withBody(message.getBytes()).build();

        logger.info("CorrelationId Before: {}" , newMessage.getMessageProperties().getCorrelationId());

        Message result = rabbitTemplate.sendAndReceive(RabbitConfig.RPC_EXCHANGE, RabbitConfig.RPC_QUEUE1, newMessage);
        logger.info("CorrelationId Before: {}" , newMessage.getMessageProperties().getCorrelationId());
        String response = "";
        if (result != null) {
            String correlationId = newMessage.getMessageProperties().getCorrelationId();
            logger.info("correlationId:{}", correlationId);

            HashMap<String, Object> headers = (HashMap<String, Object>) result.getMessageProperties().getHeaders();

            String msgId = (String) headers.get("spring_returned_message_correlation");

            if (msgId.equals(correlationId)) {
                response = new String(result.getBody());
                logger.info("client receive：{}", response);
                logger.info("client msg：{}", correlationId);
                logger.info("server msg：{}", result.getMessageProperties().getCorrelationId());
            }
        }
        return response;
    }
}

