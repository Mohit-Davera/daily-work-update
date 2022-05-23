package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RpcServerController {
    private static final Logger logger = LoggerFactory.getLogger(RpcServerController.class);
    
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitConfig.RPC_QUEUE1)
    public void process(Message msg) {
        logger.info("Server Receive : {}",new String(msg.getBody()));
        int number = Integer.parseInt(new String(msg.getBody()));
        logger.info(new String(msg.getBody()));
        int r=fib(number);
        logger.info("Calculated: {}",r);
        Message response = MessageBuilder.withBody((r+ "").getBytes()).build();
        logger.info("Server CorrelationId {}",(response.getMessageProperties().getCorrelationId()));
        CorrelationData correlationData = new CorrelationData(msg.getMessageProperties().getCorrelationId());
        rabbitTemplate.sendAndReceive(RabbitConfig.RPC_EXCHANGE, RabbitConfig.RPC_QUEUE2, response, correlationData);
        
    }
    
    public int fib(int n) {
    	int a = 0, b = 1, c, i;
        if( n == 0)
            return a;
        for(i = 2; i <= n; i++)
        {
           c = a + b;
           a = b;
           b = c;
        }
        return b;
    }
    
}
