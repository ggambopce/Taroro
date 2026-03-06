package com.neocompany.taroro.domain.point.docs;

import org.springframework.http.ResponseEntity;

import com.neocompany.taroro.domain.point.dto.PointChargeConfirmRequestDto;
import com.neocompany.taroro.domain.point.dto.PointChargeReadyRequestDto;
import com.neocompany.taroro.domain.point.dto.PointChargeReadyResponseDto;
import com.neocompany.taroro.global.oauth2.PrincipalDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Point", description = "포인트 충전 API (Toss Payments)")
public interface PointChargeControllerDocs {

    @Operation(
        summary = "포인트 충전 준비",
        description = "Toss 결제창을 열기 전에 서버에 주문 정보를 등록합니다. 반환된 `orderId`를 Toss SDK에 전달하세요."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "준비 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "충전 준비 완료",
                      "statusCode": 200,
                      "data": {
                        "chargeId": 1,
                        "orderId": "550e8400-e29b-41d4-a716-446655440000",
                        "amount": 10000
                      }
                    }
                    """)
            )
        ),
        @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    ResponseEntity<com.neocompany.taroro.global.response.ApiResponse<PointChargeReadyResponseDto>> ready(
        @Parameter(hidden = true) PrincipalDetails principal,
        PointChargeReadyRequestDto body
    );

    // -------------------------------------------------------------------------

    @Operation(
        summary = "포인트 충전 확정",
        description = """
            Toss 결제 완료 후 서버에서 결제를 최종 승인합니다.
            - Toss API 호출 → DB 반영 (point_charge, point_wallet, point_ledger)
            - 멱등성 보장: 이미 PAID 상태이면 중복 처리 없이 200 반환
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "충전 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "포인트 충전 완료",
                      "statusCode": 200
                    }
                    """)
            )
        ),
        @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    ResponseEntity<com.neocompany.taroro.global.response.ApiResponse<Void>> confirm(
        PointChargeConfirmRequestDto body
    );
}
