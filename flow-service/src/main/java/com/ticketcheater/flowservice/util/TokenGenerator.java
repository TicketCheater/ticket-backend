package com.ticketcheater.flowservice.util;

import com.ticketcheater.flowservice.exception.ErrorCode;
import com.ticketcheater.flowservice.exception.FlowApplicationException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TokenGenerator {

    private static final String ALGORITHM = "SHA-256";

    private TokenGenerator() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String generateToken(String gameId, String name) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(ALGORITHM);
            String input = "member-queue-%s-%s".formatted(gameId, name);
            byte[] encodedHash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for(byte b : encodedHash) hexString.append(String.format("%02x", b));
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new FlowApplicationException(ErrorCode.NO_SUCH_ALGORITHM, String.format("%s is invalid", ALGORITHM));
        }
    }

}
