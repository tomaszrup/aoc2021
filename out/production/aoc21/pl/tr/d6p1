package pl.tr;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private static final int DAYS = 80;

    public static void main(String[] args) {
        long startTime = System.nanoTime();

        var dayFish = new HashMap<Integer, Long>();

        try (var stream = Files.lines(Paths.get("input.txt"))) {
            var fishes = stream.findFirst().get().toCharArray();

            for(var day : fishes) {
                if(day == ',') continue;
                var key = day - '0';
                var fishesForDay = dayFish.getOrDefault(key, 0L);
                dayFish.put(key, fishesForDay + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for(var day = 0; day < DAYS; day++) {
            var newDayFish = new HashMap<Integer, Long>();
            dayFish.entrySet().forEach(entry -> newDayFish.put(entry.getKey() - 1, entry.getValue()));

            var resettingFish = newDayFish.getOrDefault(-1, 0L);
            newDayFish.put(8, resettingFish);
            newDayFish.put(6, newDayFish.getOrDefault(6, 0L) + resettingFish);
            newDayFish.put(-1, 0L);
            dayFish = newDayFish;
        }

        System.out.println(dayFish.values().stream().reduce(Long::sum).get());
        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.println("total time: " + totalTime);
    }

}
