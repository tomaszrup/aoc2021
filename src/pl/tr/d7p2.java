package pl.tr;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class Main {


    public static void main(String[] args) {
        long startTime = System.nanoTime();
        try (var stream = Files.lines(Paths.get("input.txt"))) {


            stream.forEach(line -> {
                var positions = Arrays.stream(line.split(","))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());

                var minPos = positions.stream().mapToInt(c -> c).min().orElseThrow();
                var maxPos = positions.stream().mapToInt(c -> c).max().orElseThrow();

                var posToFuel = new HashMap<Integer, Integer>();

                for(var i = minPos; i <= maxPos; i++) {
                    var finalI = i;
                    var val = positions.stream().reduce(0, (t, next) -> {
                        var movesNeeded = Math.abs(next - finalI);
                        var moveCost = 1;
                        var sum = 0;
                        for(var x = 0; x < movesNeeded; x++) {
                            sum += moveCost++;
                        }
                        return t + sum;
                    });
                    posToFuel.put(i, val);
                }

                var minFuel = posToFuel.entrySet().stream()
                        .min(Comparator.comparingInt(Map.Entry::getValue))
                        .orElseThrow().getValue();


                System.out.println(minFuel);

            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.println("total time: " + totalTime);
    }

}
