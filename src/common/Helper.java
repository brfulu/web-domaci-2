package common;

import java.util.Random;

public class Helper {
    public static Random r = new Random();

    public static int randomInt(int min, int max) {
        return min + r.nextInt((max - min) + 1);
    }

    public static boolean randomBoolean() {
        return r.nextBoolean();
    }
}