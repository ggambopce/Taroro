package com.neocompany.taroro.domain.admin.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neocompany.taroro.domain.payment.service.ConsultationPaymentService;
import com.neocompany.taroro.global.response.GlobalApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/consultation-payments")
@RequiredArgsConstructor
public class AdminConsultationPaymentController {

    private final ConsultationPaymentService paymentService;

    @PostMapping("/{paymentId}/refund")
    public GlobalApiResponse<?> refund(@PathVariable Long paymentId) {
        paymentService.refund(paymentId);
        return GlobalApiResponse.ok("환불 완료", null);
    }
}
