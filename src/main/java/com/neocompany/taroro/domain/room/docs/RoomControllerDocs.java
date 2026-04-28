package com.neocompany.taroro.domain.room.docs;

import com.neocompany.taroro.domain.room.dto.CreateRoomRequest;
import com.neocompany.taroro.domain.room.dto.RoomDetailResponse;
import com.neocompany.taroro.domain.room.dto.RoomSummaryResponse;
import com.neocompany.taroro.domain.room.dto.UpdateRoomRequest;
import com.neocompany.taroro.global.dto.PageResult;
import com.neocompany.taroro.global.oauth2.PrincipalDetails;
import com.neocompany.taroro.global.response.GlobalApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Room", description = "상담방 API")
public interface RoomControllerDocs {

    @Operation(
        summary = "상담방 생성",
        description = """
            승인된 타로마스터만 상담방을 생성할 수 있습니다.
            - 생성된 방은 `WAITING` 상태로 시작됩니다.
            - 생성 즉시 대기열(`GET /api/waiting-room`)에 노출됩니다.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "생성 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = GlobalApiResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "상담방 생성 성공",
                      "statusCode": 200,
                      "result": {
                        "id": 1,
                        "masterId": 10,
                        "masterName": "타로마스터홍길동",
                        "roomName": "타로 상담실",
                        "status": "WAITING",
                        "startedAt": null,
                        "endedAt": null,
                        "createdAt": "2026-04-28T10:00:00Z",
                        "participants": []
                      }
                    }
                    """))),
        @ApiResponse(responseCode = "200", description = "마스터 미승인 또는 미등록",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {"success": false, "message": "승인된 마스터만 방을 생성할 수 있습니다.", "statusCode": 201}
                    """)))
    })
    GlobalApiResponse<RoomDetailResponse> createRoom(
        CreateRoomRequest request,
        @Parameter(hidden = true) PrincipalDetails principal
    );

    @Operation(
        summary = "상담방 상세 조회",
        description = """
            방의 기본 정보와 현재 참여자 목록을 조회합니다.
            - `role`: 참여자 역할 (`MASTER` / `USER`)
            - `isOnline`: 현재 온라인 여부 (STOMP 퇴장 시 false)
            - 방 참여자만 조회 가능합니다.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = GlobalApiResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "상담방 조회 성공",
                      "statusCode": 200,
                      "result": {
                        "id": 1,
                        "masterId": 10,
                        "masterName": "타로마스터홍길동",
                        "roomName": "타로 상담실",
                        "status": "ACTIVE",
                        "startedAt": "2026-04-28T10:05:00Z",
                        "endedAt": null,
                        "createdAt": "2026-04-28T10:00:00Z",
                        "participants": [
                          {
                            "userId": 10,
                            "userName": "타로마스터홍길동",
                            "role": "MASTER",
                            "isOnline": true
                          },
                          {
                            "userId": 31,
                            "userName": "강진호",
                            "role": "USER",
                            "isOnline": true
                          }
                        ]
                      }
                    }
                    """))),
        @ApiResponse(responseCode = "200", description = "접근 권한 없음",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {"success": false, "message": "상담방 접근 권한이 없습니다.", "statusCode": 201}
                    """)))
    })
    GlobalApiResponse<RoomDetailResponse> getRoom(
        Long roomId,
        @Parameter(hidden = true) PrincipalDetails principal
    );

    @Operation(
        summary = "내 상담방 목록 조회",
        description = """
            내가 생성하거나 참여한 모든 상담방 목록을 반환합니다.
            - `lastMessage`: 마지막 메시지 내용 (없으면 null)
            - `lastMessageAt`: 마지막 메시지 시각 (없으면 null)
            - `unreadCount`: 읽지 않은 메시지 수
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = GlobalApiResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "상담방 목록 조회 성공",
                      "statusCode": 200,
                      "result": {
                        "items": [
                          {
                            "id": 1,
                            "masterId": 10,
                            "masterName": "타로마스터홍길동",
                            "roomName": "타로 상담실",
                            "status": "ACTIVE",
                            "lastMessage": "상담 요청드립니다.",
                            "lastMessageAt": "2026-04-28T10:31:00Z",
                            "unreadCount": 2,
                            "createdAt": "2026-04-28T10:00:00Z"
                          }
                        ],
                        "limit": 20,
                        "offset": 0,
                        "hasNext": false
                      }
                    }
                    """)))
    })
    GlobalApiResponse<PageResult<RoomSummaryResponse>> getMyRooms(
        int limit,
        int offset,
        @Parameter(hidden = true) PrincipalDetails principal
    );

    @Operation(
        summary = "상담방 수정",
        description = "방 마스터만 방 이름을 수정할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {"success": true, "message": "상담방 수정 성공", "statusCode": 200}
                    """))),
        @ApiResponse(responseCode = "200", description = "권한 없음",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {"success": false, "message": "상담방 접근 권한이 없습니다.", "statusCode": 201}
                    """)))
    })
    GlobalApiResponse<?> updateRoom(
        Long roomId,
        UpdateRoomRequest request,
        @Parameter(hidden = true) PrincipalDetails principal
    );

    @Operation(
        summary = "상담방 종료",
        description = """
            방 마스터만 상담방을 종료(`CLOSED`)할 수 있습니다.
            - `WAITING` 또는 `ACTIVE` 상태 모두 종료 가능합니다.
            - 이미 `CLOSED`인 방은 오류를 반환합니다.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "종료 성공",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {"success": true, "message": "상담방 종료 성공", "statusCode": 200}
                    """))),
        @ApiResponse(responseCode = "200", description = "이미 종료된 방",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {"success": false, "message": "이미 종료된 방입니다.", "statusCode": 201}
                    """)))
    })
    GlobalApiResponse<?> closeRoom(
        Long roomId,
        @Parameter(hidden = true) PrincipalDetails principal
    );
}
