package com.nextpage.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    private String code;
    // todo
    // user:USER001 - notion USER0001은 찾을 수 없음.
    private String message;
    private Object data;
}
