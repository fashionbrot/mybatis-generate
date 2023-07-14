package com.github.fashionbrot.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DatabaseRequest {

    private String name;

    private String driverClassName;

    private String url;

    private String username;

    private String password;

}
