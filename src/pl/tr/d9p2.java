package pl.tr;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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

        var basinSizes = new ArrayList<Integer>();


        for (var row = 0; row < numbers.size(); row++) {
            for (var col = 0; col < numbers.get(0).size(); col++) {
                basinSizes.add(flood(numbers, row, col));
            }
        }


        var atomic = new AtomicInteger();

        var product = basinSizes.stream()
                .sorted(Comparator.reverseOrder())
                .takeWhile(p -> atomic.incrementAndGet() <= 3)
                .reduce((b1, b2) -> b1 * b2)
                .get();

        System.out.println(product);

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("total time (ms): " + totalTime);
    }

    private static int flood(List<List<Integer>> nums, int row, int col) {
        var basinSize = 0;

        int val = getValueAt(nums, row, col);
        if (val < 9) {
            basinSize++;

            try {
                var r2 = nums.get(row);
                r2.set(col, 9);
                nums.set(row, r2);
            } catch (Exception e) {}

            basinSize += flood(nums, row + 1, col);
            basinSize += flood(nums, row - 1, col);
            basinSize += flood(nums, row, col + 1);
            basinSize += flood(nums, row, col - 1);
        }
        return basinSize;
    }

    private static int getValueAt(List<List<Integer>> nums, int row, int col) {
        if (row < 0 || col < 0 || row >= nums.size() || col >= nums.get(0).size()) {
            return 9;
        } else {
            return nums.get(row).get(col);
        }
    }

}
