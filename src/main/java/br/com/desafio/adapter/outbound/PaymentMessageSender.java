package br.com.desafio.adapter.outbound;

import br.com.desafio.infraestructure.dto.MessageSenderDto;
import br.com.desafio.infraestructure.messaging.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentMessageSender implements MessageSender {

  private final RabbitTemplate rabbitTemplate;

  @Value("${exchangeName}")
  private String exchangeName;

  @Override
  public void sendMessage(MessageSenderDto message) {
    rabbitTemplate.convertAndSend(exchangeName, message.getRoutingKey(), message.getMessage());
  }
}
