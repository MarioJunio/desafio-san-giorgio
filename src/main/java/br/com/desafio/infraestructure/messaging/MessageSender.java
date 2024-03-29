package br.com.desafio.infraestructure.messaging;

import br.com.desafio.infraestructure.dto.MessageSenderDto;

public interface MessageSender {

  void sendMessage(MessageSenderDto message);
}
