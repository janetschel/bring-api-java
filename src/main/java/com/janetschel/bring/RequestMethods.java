package com.janetschel.bring;

import lombok.Getter;

public enum RequestMethods {
    GET("GET"),
    POST("POST"),
    PUT("PUT");

    @Getter private final String value;
    RequestMethods(String value) {
        this.value = value;
    }
}
