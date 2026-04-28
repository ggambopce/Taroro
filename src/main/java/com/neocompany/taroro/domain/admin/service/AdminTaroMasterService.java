package com.neocompany.taroro.domain.admin.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neocompany.taroro.domain.admin.dto.request.MasterApprovalRequest;
import com.neocompany.taroro.domain.taromaster.dto.response.TaroMasterResponse;
import com.neocompany.taroro.domain.taromaster.entity.ApprovalStatus;
import com.neocompany.taroro.domain.taromaster.entity.MasterStatus;
import com.neocompany.taroro.domain.taromaster.entity.TaroMaster;
import com.neocompany.taroro.domain.taromaster.repository.TaroMasterRepository;
import com.neocompany.taroro.domain.users.UserRepository;
import com.neocompany.taroro.global.dto.PageResult;
import com.neocompany.taroro.global.exception.BusinessException;
import com.neocompany.taroro.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminTaroMasterService {

    private final TaroMasterRepository masterRepository;
    private final UserRepository userRepository;

    public PageResult<TaroMasterResponse> getAllMasters(String keyword, String status,
                                                         int limit, int offset) {
        MasterStatus masterStatus = (status != null) ? MasterStatus.from(status) : null;
        Slice<TaroMaster> slice = masterRepository.findAllMasters(
                keyword, masterStatus, PageRequest.of(offset / limit, limit));
        return new PageResult<>(
                slice.getContent().stream().map(TaroMasterResponse::new).toList(),
                limit, offset, slice.hasNext());
    }

    public TaroMasterResponse getMasterForAdmin(Long masterId) {
        TaroMaster master = masterRepository.findById(masterId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MASTER_NOT_FOUND));
        return new TaroMasterResponse(master);
    }

    public Map<Long, String> getDisplayNameMap(List<Long> masterIds) {
        return masterRepository.findAllById(masterIds).stream()
                .collect(Collectors.toMap(TaroMaster::getMasterId, TaroMaster::getDisplayName));
    }

    @Transactional
    public TaroMasterResponse approve(Long masterId, MasterApprovalRequest request) {
        TaroMaster master = masterRepository.findById(masterId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MASTER_NOT_FOUND));

        ApprovalStatus newStatus = ApprovalStatus.valueOf(request.getApprovalStatus());

        if (newStatus == ApprovalStatus.APPROVED) {
            master.approve();
            userRepository.findById(master.getUserId()).ifPresent(user -> user.set_taro_master(true));
        } else if (newStatus == ApprovalStatus.REJECTED) {
            master.reject();
            userRepository.findById(master.getUserId()).ifPresent(user -> user.set_taro_master(false));
        } else {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        return new TaroMasterResponse(master);
    }
}
