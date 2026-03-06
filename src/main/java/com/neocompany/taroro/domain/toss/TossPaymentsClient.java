package com.neocompany.taroro.domain.toss;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;

@Component
public class TossPaymentsClient {

    private static final String CONFIRM_URL = "https://api.tosspayments.com/v1/payments/confirm";

    private final RestClient restClient;

    public TossPaymentsClient(@Value("${toss.secretKey}") String secretKey) {
        String encoded = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        this.restClient = RestClient.builder()
                .defaultHeader("Authorization", "Basic " + encoded)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public TossConfirmResponse confirm(String paymentKey, String orderId, long amount) {
        return restClient.post()
                .uri(CONFIRM_URL)
                .body(Map.of(
                        "paymentKey", paymentKey,
                        "orderId", orderId,
                        "amount", amount
                ))
                .retrieve()
                .body(TossConfirmResponse.class);
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TossConfirmResponse {
        private String paymentKey;
        private String orderId;
        private String status;
        private String approvedAt;
        private Long totalAmount;
    }
}
