package com.neocompany.taroro.domain.message.docs;

import com.neocompany.taroro.domain.message.dto.response.ChatMessageResponse;
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

@Tag(name = "Message", description = "채팅 메시지 조회 API")
public interface MessageControllerDocs {

    @Operation(
        summary = "이전 메시지 목록 조회 (커서 기반)",
        description = """
            상담방의 메시지 목록을 커서 기반 페이징으로 조회합니다.
            - `cursor` 없으면 최신 메시지부터 `size`개 반환합니다.
            - `cursor` 있으면 해당 메시지 ID **이전** 메시지를 `size`개 반환합니다.
            - 응답의 `nextCursor`로 다음 페이지 조회 (`cursor=nextCursor` 파라미터 전달).
            - `senderRole`: 해당 메시지 발신자의 역할 (`MASTER` / `USER`)
            - `readCount`: 해당 메시지를 읽은 참여자 수
            - 방 참여자(masterId 또는 userId)만 조회 가능합니다.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = GlobalApiResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "이전 메시지 조회 성공",
                      "statusCode": 200,
                      "result": {
                        "roomId": 1,
                        "messages": [
                          {
                            "messageId": 42,
                            "roomId": 1,
                            "senderId": 10,
                            "senderName": "강진호",
                            "senderRole": "USER",
                            "messageType": "TEXT",
                            "content": "안녕하세요, 타로 상담 부탁드립니다.",
                            "createdAt": "2026-04-28T10:31:00Z",
                            "readCount": 1
                          },
                          {
                            "messageId": 41,
                            "roomId": 1,
                            "senderId": 22,
                            "senderName": "타로마스터홍길동",
                            "senderRole": "MASTER",
                            "messageType": "TEXT",
                            "content": "네, 안녕하세요! 어떤 부분이 궁금하신가요?",
                            "createdAt": "2026-04-28T10:30:00Z",
                            "readCount": 1
                          }
                        ],
                        "hasNext": true,
                        "nextCursor": 41
                      }
                    }
                    """))),
        @ApiResponse(responseCode = "200", description = "더 이상 메시지 없음",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "이전 메시지 조회 성공",
                      "statusCode": 200,
                      "result": {
                        "roomId": 1,
                        "messages": [],
                        "hasNext": false,
                        "nextCursor": null
                      }
                    }
                    """))),
        @ApiResponse(responseCode = "200", description = "접근 권한 없음",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "상담방 접근 권한이 없습니다.",
                      "statusCode": 201
                    }
                    """)))
    })
    GlobalApiResponse<ChatMessageResponse.PageResult> getMessages(
        @Parameter(description = "상담방 ID") Long roomId,
        @Parameter(description = "커서 ID — 이 ID 이전 메시지 조회 (첫 조회 시 생략)") Long cursor,
        @Parameter(description = "조회 개수 (기본값: 20)") int size,
        @Parameter(hidden = true) PrincipalDetails principal
    );
}
