package com.neocompany.taroro.domain.master;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neocompany.taroro.domain.master.dto.MasterStatusEvent;
import com.neocompany.taroro.domain.master.dto.MasterStatusRequest;
import com.neocompany.taroro.domain.taromaster.entity.MasterStatus;
import com.neocompany.taroro.domain.taromaster.repository.TaroMasterRepository;
import com.neocompany.taroro.domain.users.User;
import com.neocompany.taroro.domain.users.UserRepository;
import com.neocompany.taroro.global.exception.BusinessException;
import com.neocompany.taroro.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MasterStatusService {

    private final UserRepository userRepository;
    private final TaroMasterRepository taroMasterRepository;

    public MasterStatusEvent buildEvent(Long userId, MasterStatusRequest request) {
        User user = userRepository.findByUserIdAndDeletedFalse(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        if (!user.is_taro_master()) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        // TaroMaster.status DB 동기화 (존재할 경우만)
        MasterStatus newStatus = MasterStatus.from(request.getStatus());
        taroMasterRepository.findByUserId(userId)
                .ifPresent(master -> master.updateStatus(newStatus));

        return MasterStatusEvent.of(userId, user.getNickname(), request.getStatus());
    }
}
