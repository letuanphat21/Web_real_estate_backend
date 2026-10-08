package com.dat_viet_group.datvietgroup.core.security;

import java.util.List;

public class Endpoints {
    public static final List<String> FRONT_END_HOSTS = List.of(
            "http://localhost:5173");

    public static final String[] PUBLIC_GET_ENDPOINTS = new String[] {
            "/api/events",  
            "/api/events/**",
    };

    public static final String[] PUBLIC_POST_ENDPOINTS = new String[] {
            "/api/users/register",
            "/api/users/login",
            "/api/test/cloudinary/**", 
    };

    public static final String[] PRIVATE_GET_ENDPOINT = new String[] {

    };

    public static final String[] PRIVATE_POST_ENDPOINT = new String[] {
                
    };

    public static final String[] PRIVATE_PUT_ENDPOINT = new String[] {
    };

    public static final String[] ADMIN_PUT_ENDPOINTS = new String[] {
    };

    public static final String[] ADMIN_GET_ENDPOINTS = new String[] {
    };

}