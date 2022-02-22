package com.loanapp.loanapplication.messaging;

import com.loanapp.loanapplication.model.dto.CustomerSmsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SmsProducer {

    private final RabbitTemplate rabbitTemplate;

    public void messageOnLoanApproval(CustomerSmsDto customerSmsDto){
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, customerSmsDto);
        System.out.println("Message Sent");
    }
}
