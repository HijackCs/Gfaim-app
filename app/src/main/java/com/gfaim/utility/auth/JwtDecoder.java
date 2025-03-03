package com.gfaim.utility.auth;

import android.util.Base64;

import lombok.experimental.UtilityClass;


@UtilityClass
public class JwtDecoder {

    public static String decodeJWT(String jwt) {
        String[] parts = jwt.split("\\.");
        if (parts.length == 3) {
            String payload = parts[1];
            byte[] decodedBytes = Base64.decode(payload, Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);
            return new String(decodedBytes);
        }
        return null;
    }
}
