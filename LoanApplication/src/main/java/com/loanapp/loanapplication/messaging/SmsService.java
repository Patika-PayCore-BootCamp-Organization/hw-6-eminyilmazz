package com.loanapp.loanapplication.messaging;

import com.loanapp.loanapplication.model.dto.CustomerSmsDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class SmsService {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void smsService(CustomerSmsDto customerSmsDto){
        System.out.println("Dear " + customerSmsDto.getName() + customerSmsDto.getLastName() + "," +
                "\nYour application for a loan is approved at " + customerSmsDto.getApprovalDate().format(formatter) +
                "\nLoan amount is: " + customerSmsDto.getLoanAmount());
    }
}
