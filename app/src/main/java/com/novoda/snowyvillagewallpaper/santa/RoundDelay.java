package com.novoda.snowyvillagewallpaper.santa;

import java.util.Random;

class RoundDelay {

    private static final int SECONDS = 1000;
    private static final int MIN_INTERVAL_BETWEEN_ROUNDS = 5 * SECONDS;
    private static final int MAX_INTERVAL_BETWEEN_ROUNDS = 10 * SECONDS;

    private final Random random;

    public RoundDelay() {
        random = new Random();
    }

     public int getNextRoundDelay() {
         return MIN_INTERVAL_BETWEEN_ROUNDS
                 + random.nextInt(MAX_INTERVAL_BETWEEN_ROUNDS - MIN_INTERVAL_BETWEEN_ROUNDS);
     }
}
