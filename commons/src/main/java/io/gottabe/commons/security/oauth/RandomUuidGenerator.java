package io.gottabe.commons.security.oauth;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

public class RandomUuidGenerator {

    private Random random;

    public RandomUuidGenerator() {
        byte[] seed = new byte[16];
        new Random(System.currentTimeMillis() + System.nanoTime()).nextBytes(seed);
        this.random = new SecureRandom(seed);
    }

    public String next() {
        byte[] bs = new byte[16];
        this.random.nextBytes(bs);
        return UUID.nameUUIDFromBytes(bs).toString();
    }

}
