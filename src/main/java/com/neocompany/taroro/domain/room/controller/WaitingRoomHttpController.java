package com.neocompany.taroro.domain.room.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.neocompany.taroro.domain.room.docs.WaitingRoomControllerDocs;
import com.neocompany.taroro.domain.room.dto.WaitingRoomResponse;
import com.neocompany.taroro.domain.room.service.RoomService;
import com.neocompany.taroro.global.response.GlobalApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/waiting-room")
@RequiredArgsConstructor
public class WaitingRoomHttpController implements WaitingRoomControllerDocs {

    private final RoomService roomService;

    @Override
    @GetMapping
    public GlobalApiResponse<WaitingRoomResponse> getWaitingRooms(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return GlobalApiResponse.ok("대기열 조회 성공", roomService.getWaitingRooms(limit, offset));
    }
}
