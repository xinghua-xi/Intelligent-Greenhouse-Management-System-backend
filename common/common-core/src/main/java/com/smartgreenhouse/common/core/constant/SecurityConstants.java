package com.smartgreenhouse.common.core.constant;

public interface SecurityConstants {
    // 对应文档中的鉴权 Token 逻辑 [cite: 793]
    String HEADER = "Authorization";
    String TOKEN_PREFIX = "Bearer ";
    String SECRET_KEY = "SmartGreenhouse_SecretKey_For_JWT_Generation_Must_Be_Long";
}