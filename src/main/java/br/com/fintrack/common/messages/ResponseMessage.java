package br.com.fintrack.common.messages;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ResponseMessage implements Serializable {

    private String status;
    private String message;
    private LocalDateTime timestamp;
}
