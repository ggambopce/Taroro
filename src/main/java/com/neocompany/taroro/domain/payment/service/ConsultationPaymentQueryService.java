package com.neocompany.taroro.domain.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neocompany.taroro.domain.payment.dto.response.ConsultationPaymentResponse;
import com.neocompany.taroro.domain.payment.entity.ConsultationPayment;
import com.neocompany.taroro.domain.payment.repository.ConsultationPaymentRepository;
import com.neocompany.taroro.domain.room.entity.Room;
import com.neocompany.taroro.domain.room.repository.RoomRepository;
import com.neocompany.taroro.global.exception.BusinessException;
import com.neocompany.taroro.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsultationPaymentQueryService {

    private final ConsultationPaymentRepository paymentRepository;
    private final RoomRepository roomRepository;

    public ConsultationPaymentResponse getByRoomId(Long roomId, Long requesterId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));
        if (!room.isParticipant(requesterId)) {
            throw new BusinessException(ErrorCode.ROOM_ACCESS_DENIED);
        }
        ConsultationPayment p = paymentRepository.findByRoomId(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
        return new ConsultationPaymentResponse(p);
    }
}
