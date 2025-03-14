package com.onhz.server.controller.common;

import com.onhz.server.dto.response.ApiResponse;
import com.onhz.server.dto.response.CodeResponse;
import com.onhz.server.service.common.CodeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/code")
@RequiredArgsConstructor
public class CodeController {
    private final CodeService codeService;
    @GetMapping("/{codeType}")
    @Operation(summary = "코드 리스트", description = "")
    public ApiResponse<List<CodeResponse>> getCodes(@PathVariable String codeType) {
        List<CodeResponse> result = codeService.getCodeByType(codeType);
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }
}
