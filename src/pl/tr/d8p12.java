package pl.tr;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private static Map<Integer, String> ACTUAL_SEGMENTS_TO_NUM = new HashMap<>();

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        var inputVars = new ArrayList<List<Integer>>();
        var outputVars = new ArrayList<List<Integer>>();

        try (var stream = Files.lines(Paths.get("input.txt"))) {
            stream.forEach(line -> {
                var split = line.split("\\|");

                var input = List.of(split[0].split(" "));
                var output = List.of(split[1].split(" "));

                detectNumbers(input);

                inputVars.add(input.stream().filter(s -> !s.isEmpty()).map(Main::getNum).collect(Collectors.toList()));
                outputVars.add(output.stream().filter(s -> !s.isEmpty()).map(Main::getNum).collect(Collectors.toList()));

                ACTUAL_SEGMENTS_TO_NUM = new HashMap<>();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


        var count1 = outputVars.stream().flatMap(Collection::stream)
                .filter(c -> c == 1 || c == 4 || c == 7 || c == 8)
                .count();

        var count2 = outputVars.stream()
                .map(l -> Long.parseLong( l.stream().map(String::valueOf).reduce(String::concat).get()))
                .reduce(Long::sum).get();


        System.out.println(count1);
        System.out.println(count2);

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("total time (ms): " + totalTime);
    }

    private static void detectNumbers(List<String> x) {
        while(ACTUAL_SEGMENTS_TO_NUM.keySet().size() != 10) {
            x.forEach(Main::getNum);
        }
    }

    // XD
    private static int getNum(String x) {
        var val = -1;

        final boolean contains1 = ACTUAL_SEGMENTS_TO_NUM.containsKey(1);
        final boolean contains4 = ACTUAL_SEGMENTS_TO_NUM.containsKey(4);

        switch (x.length()) {
            case 2:
                val = 1;
                break;
            case 3:
                val = 7;
                break;
            case 4:
                val = 4;
                break;
            case 7:
                val = 8;
                break;
            case 5:
                if (contains1 && countCommon(x, ACTUAL_SEGMENTS_TO_NUM.get(1)) == 2) {
                    val = 3;
                    break;
                }

                if (contains4) {
                    if (countCommon(x, ACTUAL_SEGMENTS_TO_NUM.get(4)) == 2) {
                        val = 2;
                    } else {
                        val = 5;
                    }
                }
                break;
            case 6:
                if (contains1 && countCommon(x, ACTUAL_SEGMENTS_TO_NUM.get(1)) == 1) {
                    val = 6;
                    break;
                }

                if (contains4) {
                    if (countCommon(x, ACTUAL_SEGMENTS_TO_NUM.get(4)) == 3) {
                        val = 0;
                    } else {
                        val = 9;
                    }
                }
                break;

        }

        if (val != -1) {
            ACTUAL_SEGMENTS_TO_NUM.putIfAbsent(val, x);
        }

        return val;
    }

    private static int countCommon(String x1, String x2) {
        var x2ints = x2.chars().boxed().collect(Collectors.toList());
        return (int) x1.chars().boxed().filter(x2ints::contains).count();
    }

}
