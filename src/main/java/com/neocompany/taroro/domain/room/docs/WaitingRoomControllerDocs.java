package com.neocompany.taroro.domain.room.docs;

import com.neocompany.taroro.domain.room.dto.WaitingRoomResponse;
import com.neocompany.taroro.global.response.GlobalApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "WaitingRoom", description = "대기열 조회 API (인증 불필요)")
public interface WaitingRoomControllerDocs {

    @Operation(
        summary = "대기열 조회",
        description = """
            현재 상담 대기 중인 방 목록을 반환합니다.
            - 인증 없이 조회 가능한 공개 API입니다.
            - `waitingCount`: 현재 전체 대기방 수
            - `queueNumber`: offset 기준 순번 (offset=0이면 1부터 시작)
            - `requestedAt`: 마스터가 방을 생성한 시각
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = GlobalApiResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "대기열 조회 성공",
                      "statusCode": 200,
                      "data": {
                        "waitingCount": 3,
                        "items": [
                          {
                            "queueNumber": 1,
                            "roomId": 1,
                            "masterId": 10,
                            "masterName": "타로마스터홍길동",
                            "roomName": "타로 상담실",
                            "requestedAt": "2026-04-28T10:00:00Z"
                          },
                          {
                            "queueNumber": 2,
                            "roomId": 2,
                            "masterId": 15,
                            "masterName": "별자리마스터",
                            "roomName": "별자리 상담",
                            "requestedAt": "2026-04-28T10:05:00Z"
                          }
                        ]
                      }
                    }
                    """)))
    })
    GlobalApiResponse<WaitingRoomResponse> getWaitingRooms(
        @Parameter(description = "조회 개수 (기본 20)") int limit,
        @Parameter(description = "조회 시작 위치 (기본 0)") int offset
    );
}
