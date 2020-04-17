package com.janetschel.bring;

import lombok.Getter;

@Getter
public class User {
    private final String name;
    private final String email;

    User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
