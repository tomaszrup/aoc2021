package pl.tr;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        try (var stream = Files.lines(Paths.get("input.txt"))) {
            var zeros = new ArrayList<>(List.of(0, 0, 0,
                    0, 0, 0,
                    0, 0, 0,
                    0, 0, 0));

            var ones = new ArrayList<>(List.of(0, 0, 0,
                    0, 0, 0,
                    0, 0, 0,
                    0, 0, 0));

            int gamma = 0;
            int epsilon = 0;

            stream.forEach(line -> {
                var arr = line.toCharArray();
                for(var i = 0; i < zeros.size(); i++) {
                    if(arr[i] == '0') {
                        zeros.set(i, zeros.get(i) + 1);
                    } else {
                        ones.set(i, ones.get(i) + 1);
                    }
                }
            });

            for(var i = 0; i < zeros.size(); i++) {

                var value = Math.pow(2, zeros.size() - 1 - i);

                if(zeros.get(i) > ones.get(i)) {
                    epsilon += value;
                } else {
                    gamma += value;
                }
            }

            System.out.println(zeros);
            System.out.println(ones);
            System.out.println(epsilon * gamma);


        } catch (Exception e) {
            e.printStackTrace();
        }
        long endTime   = System.nanoTime();
        long totalTime = endTime - startTime;
    }
}
