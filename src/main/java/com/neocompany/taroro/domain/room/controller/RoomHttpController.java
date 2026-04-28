package com.neocompany.taroro.domain.room.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.neocompany.taroro.domain.room.docs.RoomControllerDocs;
import com.neocompany.taroro.domain.room.dto.CreateRoomRequest;
import com.neocompany.taroro.domain.room.dto.RoomDetailResponse;
import com.neocompany.taroro.domain.room.dto.RoomSummaryResponse;
import com.neocompany.taroro.domain.room.dto.UpdateRoomRequest;
import com.neocompany.taroro.domain.room.service.RoomCommandService;
import com.neocompany.taroro.domain.room.service.RoomService;
import com.neocompany.taroro.global.dto.PageResult;
import com.neocompany.taroro.global.oauth2.PrincipalDetails;
import com.neocompany.taroro.global.response.GlobalApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomHttpController implements RoomControllerDocs {

    private final RoomService roomService;
    private final RoomCommandService roomCommandService;

    @Override
    @PostMapping
    public GlobalApiResponse<RoomDetailResponse> createRoom(
            @RequestBody CreateRoomRequest request,
            @AuthenticationPrincipal PrincipalDetails principal) {
        Long userId = principal.getUser().getUserId();
        var room = roomCommandService.create(userId, request);
        var response = roomService.getRoomDetail(room.getId(), userId);
        return GlobalApiResponse.ok("상담방 생성 성공", response);
    }

    @Override
    @GetMapping("/{roomId}")
    public GlobalApiResponse<RoomDetailResponse> getRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal PrincipalDetails principal) {
        return GlobalApiResponse.ok("상담방 조회 성공",
                roomService.getRoomDetail(roomId, principal.getUser().getUserId()));
    }

    @Override
    @GetMapping
    public GlobalApiResponse<PageResult<RoomSummaryResponse>> getMyRooms(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset,
            @AuthenticationPrincipal PrincipalDetails principal) {
        return GlobalApiResponse.ok("상담방 목록 조회 성공",
                roomService.getMyRooms(principal.getUser().getUserId(), limit, offset));
    }

    @Override
    @PatchMapping("/{roomId}")
    public GlobalApiResponse<?> updateRoom(
            @PathVariable Long roomId,
            @RequestBody UpdateRoomRequest request,
            @AuthenticationPrincipal PrincipalDetails principal) {
        roomCommandService.update(roomId, principal.getUser().getUserId(), request);
        return GlobalApiResponse.ok("상담방 수정 성공", null);
    }

    @Override
    @DeleteMapping("/{roomId}")
    public GlobalApiResponse<?> closeRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal PrincipalDetails principal) {
        roomCommandService.closeRoom(roomId, principal.getUser().getUserId());
        return GlobalApiResponse.ok("상담방 종료 성공", null);
    }
}
