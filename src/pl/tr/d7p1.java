package pl.tr;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
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

                var fuelCosts = new ArrayList<Integer>();

                for(var i = minPos; i <= maxPos; i++) {
                    final var p = i;
                    var val = positions.stream().reduce(0, (t, next) -> t + Math.abs(next - p));
                    fuelCosts.add(val);
                }

                var minFuel = fuelCosts.stream().mapToInt(c -> c)
                        .min().orElseThrow();

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
