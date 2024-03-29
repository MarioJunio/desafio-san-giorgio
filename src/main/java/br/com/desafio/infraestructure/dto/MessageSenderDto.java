package br.com.desafio.infraestructure.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageSenderDto {

    private String routingKey;
    private String message;
}
