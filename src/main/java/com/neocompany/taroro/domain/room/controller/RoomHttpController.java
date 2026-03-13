package com.neocompany.taroro.domain.room.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neocompany.taroro.domain.room.dto.RoomResponse;
import com.neocompany.taroro.domain.room.service.RoomService;
import com.neocompany.taroro.global.oauth2.PrincipalDetails;
import com.neocompany.taroro.global.response.GlobalApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomHttpController {

    private final RoomService roomService;

    /** 방 상세 조회 — 참여자만 가능 */
    @GetMapping("/{roomId}")
    public GlobalApiResponse<RoomResponse> getRoom(@PathVariable Long roomId,
                                                   @AuthenticationPrincipal PrincipalDetails principal) {
        return GlobalApiResponse.ok("상담방 조회 성공",
                roomService.getRoom(roomId, principal.getUser().getUserId()));
    }

    /** 내 방 목록 조회 */
    @GetMapping
    public GlobalApiResponse<List<RoomResponse>> getMyRooms(@AuthenticationPrincipal PrincipalDetails principal) {
        return GlobalApiResponse.ok("상담방 목록 조회 성공",
                roomService.getMyRooms(principal.getUser().getUserId()));
    }
}
