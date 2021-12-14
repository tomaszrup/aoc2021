package pl.tr;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    private static final HashMap<String, String> RULES = new HashMap<>();
    private static final int STEPS = 40;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        try (var stream = Files.lines(Paths.get("input.txt"))) {
            var polymerAtomic = new AtomicReference<>("");

            stream.forEach(line -> {
                if (line.contains("->")) {
                    var split = line.split("->");
                    RULES.put(split[0].trim(), split[1].trim());
                } else if (!line.isEmpty()) {
                    polymerAtomic.set(line);
                }
            });

            var polymer = polymerAtomic.get();

            var pairs = IntStream
                    .range(0, polymer.length() - 1)
                    .mapToObj(i -> polymer.substring(i, i + 2))
                    .collect(Collectors.groupingBy(Function.identity(), HashMap::new, Collectors.counting()));

            var charCount = new HashMap<String, Long>();

            polymer.chars().boxed().map(c -> (char) c.intValue())
                    .map(String::valueOf)
                    .forEach(c ->
                            charCount.put(c, charCount.getOrDefault(c, 0L) + 1));

            for(var step = 0; step < STEPS; step++) {
                var newPairs = new HashMap<String, Long>();

                pairs.forEach((pair, count) -> {
                    var c1 = String.valueOf(pair.charAt(0));
                    var c2 = String.valueOf(pair.charAt(1));

                    var created = RULES.get(c1.concat(c2));

                    charCount.put(created, charCount.getOrDefault(created, 0L) + count);

                    newPairs.put(c1.concat(created), newPairs.getOrDefault(c1.concat(created), 0L) + count);
                    newPairs.put(created.concat(c2), newPairs.getOrDefault(created.concat(c2), 0L) + count);
                });

                pairs = newPairs;
            }

            var sorted = charCount.values().stream().sorted().collect(Collectors.toList());
            System.out.println(sorted.get(sorted.size() - 1) - sorted.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }


        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("total time (ms): " + totalTime);
    }

}



