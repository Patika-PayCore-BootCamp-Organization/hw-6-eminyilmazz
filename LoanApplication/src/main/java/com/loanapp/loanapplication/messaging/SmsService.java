package com.loanapp.loanapplication.messaging;

import com.loanapp.loanapplication.config.RabbitMQConfig;
import com.loanapp.loanapplication.model.dto.CustomerSmsDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;

@Service
public class SmsService {

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void smsService(CustomerSmsDto customerSmsDto){
        System.out.println("Message sent to phone number: " + customerSmsDto.getPhoneNumber() + "\n");
        System.out.println("Dear " + StringUtils.capitalize(customerSmsDto.getName()) + " " + customerSmsDto.getLastName().toUpperCase() + "," +
                "\nYour application for a loan is approved at " +
                "\nLoan amount is: " + customerSmsDto.getLoanAmount());
    }
}
