package com.ticketcheater.webservice.dto;

import com.ticketcheater.webservice.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class PaymentDTO {

    private Long id;
    private Long memberId;
    private String method;
    private int amount;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static PaymentDTO toDTO(Payment payment) {
        return new PaymentDTO(
                payment.getId(),
                payment.getMember().getId(),
                payment.getMethod().toString(),
                payment.getAmount(),
                payment.getCreatedAt(),
                payment.getUpdatedAt(),
                payment.getDeletedAt()
        );
    }
}
