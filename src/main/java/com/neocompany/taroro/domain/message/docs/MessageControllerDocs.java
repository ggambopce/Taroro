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
        summary = "메시지 목록 조회 (커서 기반 페이징)",
        description = """
            상담방의 메시지 목록을 커서 기반 페이징으로 조회합니다.
            - `cursor` 없으면 최신 메시지부터 `size`개 반환
            - `cursor` 있으면 해당 메시지 ID 이전 메시지를 `size`개 반환
            - 응답의 `nextCursor`로 다음 페이지 조회 가능
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GlobalApiResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "메시지 목록 조회 성공",
                      "statusCode": 200,
                      "result": {
                        "messages": [
                          {
                            "id": 42,
                            "roomId": 1,
                            "senderId": 10,
                            "content": "안녕하세요",
                            "messageType": "TEXT",
                            "createdAt": "2026-03-16T10:10:00Z"
                          }
                        ],
                        "hasNext": false,
                        "nextCursor": null
                      }
                    }
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "200",
            description = "접근 권한 없음",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "상담방 접근 권한이 없습니다.",
                      "statusCode": 201
                    }
                    """)
            )
        )
    })
    GlobalApiResponse<ChatMessageResponse.PageResult> getMessages(
        @Parameter(description = "상담방 ID") Long roomId,
        @Parameter(description = "커서 (이전 응답의 nextCursor 값, 첫 조회 시 생략)") Long cursor,
        @Parameter(description = "조회 개수 (기본값: 20)") int size,
        @Parameter(hidden = true) PrincipalDetails principal
    );
}
