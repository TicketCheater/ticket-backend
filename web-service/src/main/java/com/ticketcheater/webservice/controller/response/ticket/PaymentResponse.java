package com.ticketcheater.webservice.controller.response.ticket;

import com.ticketcheater.webservice.dto.PaymentDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentResponse {
    private Long paymentId;
    private Long memberId;

    public static PaymentResponse from(PaymentDTO dto) {
        return new PaymentResponse(dto.getId(), dto.getMemberId());
    }
}
