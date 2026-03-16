package com.neocompany.taroro.domain.room.docs;

import java.util.List;

import com.neocompany.taroro.domain.room.dto.RoomResponse;
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

@Tag(name = "Room", description = "상담방 조회 API")
public interface RoomControllerDocs {

    @Operation(
        summary = "상담방 상세 조회",
        description = "방 ID로 상담방 정보를 조회합니다. 해당 방의 참여자만 조회 가능합니다."
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
                      "message": "상담방 조회 성공",
                      "statusCode": 200,
                      "result": {
                        "id": 1,
                        "userId": 10,
                        "masterId": 20,
                        "status": "WAITING",
                        "startedAt": null,
                        "endedAt": null,
                        "createdAt": "2026-03-16T10:00:00Z"
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
        ),
        @ApiResponse(
            responseCode = "200",
            description = "방을 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "상담방을 찾을 수 없습니다.",
                      "statusCode": 201
                    }
                    """)
            )
        )
    })
    GlobalApiResponse<RoomResponse> getRoom(
        Long roomId,
        @Parameter(hidden = true) PrincipalDetails principal
    );

    @Operation(
        summary = "내 상담방 목록 조회",
        description = "로그인한 사용자가 참여한 모든 상담방 목록을 반환합니다."
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
                      "message": "상담방 목록 조회 성공",
                      "statusCode": 200,
                      "result": [
                        {
                          "id": 1,
                          "userId": 10,
                          "masterId": 20,
                          "status": "CLOSED",
                          "startedAt": "2026-03-16T10:05:00Z",
                          "endedAt": "2026-03-16T10:30:00Z",
                          "createdAt": "2026-03-16T10:00:00Z"
                        }
                      ]
                    }
                    """)
            )
        )
    })
    GlobalApiResponse<List<RoomResponse>> getMyRooms(
        @Parameter(hidden = true) PrincipalDetails principal
    );
}
