package pl.tr;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        try (var stream = Files.lines(Paths.get("input.txt"))) {
            var data = stream.collect(Collectors.toList());

            var oxygen = calc(data, true, '1');
            var co2 = calc(data, false, '0');
            System.out.println(oxygen * co2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long endTime   = System.nanoTime();
        long totalTime = endTime - startTime;
    }

    private static int calc(List<String> data, boolean mostCommon, char def) {
        var currentIndex = new AtomicInteger();

        while (data.size() > 1) {
            var zeros = new AtomicInteger();
            var ones = new AtomicInteger();

            data.forEach(num -> {
                if (num.charAt(currentIndex.get()) == '0') {
                    zeros.incrementAndGet();
                } else {
                    ones.incrementAndGet();
                }
            });

            data = data.stream().filter(num -> {
                if (zeros.get() == ones.get()) {
                    return num.charAt(currentIndex.get()) == def;
                }

                var mostCommonChar = zeros.get() > ones.get() ? '0' : '1';
                return mostCommon == (num.charAt(currentIndex.get()) == mostCommonChar);
            }).collect(Collectors.toList());

            currentIndex.incrementAndGet();
        }


        var foundNum = data.get(0).toCharArray();
        System.out.println(foundNum);
        var value = 0;

        for (var i = 0; i < foundNum.length; i++) {
            if (foundNum[i] == '1') {
                value += Math.pow(2, foundNum.length - 1 - i);
            }
        }

        System.out.println(value);
        return value;
    }
}
