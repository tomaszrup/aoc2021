package pl.tr;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        var numbers = new ArrayList<List<Integer>>();

        try (var stream = Files.lines(Paths.get("input.txt"))) {
            stream.forEach(line -> {
                var lineNumbers = line.chars().map(c -> c - '0').boxed().collect(Collectors.toList());
                numbers.add(lineNumbers);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        var lowestPoints = new ArrayList<Integer>();

        for(var row = 0; row < numbers.size(); row++) {
            for(var col = 0; col < numbers.get(0).size(); col++) {
                var current = numbers.get(row).get(col);
                if(getLowestAdjacent(numbers, row, col) > current) {
                    System.out.println("row: " + row + " col: " + col + " val: " + current + " lowestAdj: " + getLowestAdjacent(numbers, row, col));
                    lowestPoints.add(current);
                }
            }
        }

        var sum = lowestPoints.stream().reduce(lowestPoints.size(), Integer::sum);

        System.out.println(sum);


        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("total time (ms): " + totalTime);
    }

    private static int getLowestAdjacent(List<List<Integer>> numbers, int row, int col) {
        var starting = 10;

        try {
            var n = numbers.get(row - 1).get(col);
            if (n < starting) {
                starting = n;
            }
        } catch (Exception e) {
        }

        try {
            var n = numbers.get(row + 1).get(col);
            if (n < starting) {
                starting = n;
            }
        } catch (Exception e) {
        }

        try {
            var n = numbers.get(row).get(col - 1);
            if (n < starting) {
                starting = n;
            }
        } catch (Exception e) {
        }

        try {
            var n = numbers.get(row).get(col + 1);
            if (n < starting) {
                starting = n;
            }
        } catch (Exception e) {
        }

        return starting;
    }
}
