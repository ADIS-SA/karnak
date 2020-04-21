package org.karnak.profile.action;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class RandomUtils {
    public static String generateAlphanumeric(int targetStringLength, Random random) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return generatedString;
    }

    public static int createRandomIntBetween(int min, int max, Random random) {
        return random.nextInt(max-min) + min;
    }

    public static String randomDA(Random random) {
        int day = createRandomIntBetween(1, 28, random);
        int month = createRandomIntBetween(1, 12, random);
        int year = createRandomIntBetween(1970, LocalDate.now().getYear(), random);
        LocalDate date = LocalDate.of(year, month, day);
        return DAtoString(date);
    }

    public static String DAtoString(LocalDate date) {
        DateTimeFormatter formatDA = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = formatDA.format(date);
        return formattedDate;
    }

    public static String randomTM(Random random) {
        int hour = createRandomIntBetween(0, 23, random);
        int minute = createRandomIntBetween(0, 59, random);
        int second = createRandomIntBetween(0, 59, random);
        LocalTime time = LocalTime.of(hour, minute, second);
        return TMtoString(time);
    }

    public static String TMtoString(LocalTime time) {
        DateTimeFormatter formatTM = DateTimeFormatter.ofPattern("HHmmss");
        String formattedTM = formatTM.format(time);
        return formattedTM;
    }

    public static String randomDT(Random random) {
        String stringDateTime = randomDA(random);
        stringDateTime = stringDateTime.concat(randomTM(random));
        return stringDateTime;
    }
}