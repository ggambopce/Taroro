package com.neocompany.taroro.domain.master;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neocompany.taroro.domain.master.dto.MasterStatusEvent;
import com.neocompany.taroro.domain.master.dto.MasterStatusRequest;
import com.neocompany.taroro.domain.users.User;
import com.neocompany.taroro.domain.users.UserRepository;
import com.neocompany.taroro.global.exception.BusinessException;
import com.neocompany.taroro.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MasterStatusService {

    private final UserRepository userRepository;

    public MasterStatusEvent buildEvent(Long userId, MasterStatusRequest request) {
        User user = userRepository.findByUserIdAndDeletedFalse(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        if (!user.is_taro_master()) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        return MasterStatusEvent.of(userId, user.getNickname(), request.getStatus());
    }
}
