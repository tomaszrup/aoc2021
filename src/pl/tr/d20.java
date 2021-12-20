package pl.tr;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private static final int ITERATIONS = 50;
    private static String ENHANCEMENT = "";

    public static void main(String[] ars) {

        var input = new ArrayList<String>();

        try (var stream = Files.lines(Paths.get("input.txt"))) {
            final var finalInput = input;
            stream.forEachOrdered(line -> {
                if (ENHANCEMENT.isEmpty()) {
                    ENHANCEMENT = line;
                } else if (!line.isEmpty()) {
                    finalInput.add(line);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (var it = 0; it < ITERATIONS; it++) {
            var paddedInput = addPadding(input, it);
            var output = new ArrayList<String>();

            for (var row = 1; row < paddedInput.size() - 1; row++) {
                var newLine = "";
                for (var col = 1; col < paddedInput.get(0).length() - 1; col++) {
                    var str = paddedInput.get(row - 1).substring(col - 1, col + 2)
                            + paddedInput.get(row).substring(col - 1, col + 2)
                            + paddedInput.get(row + 1).substring(col - 1, col + 2);
                    newLine = newLine.concat(String.valueOf(ENHANCEMENT.charAt(toIndex(str))));
                }
                output.add(newLine);
            }

            var count = output.stream().flatMap(s -> s.chars()
                            .boxed()
                            .map(c -> (char) c.intValue()))
                    .filter(c -> c == '#')
                    .count();
            System.out.println(count);
            input = new ArrayList<>(output);
        }

    }

    public static List<String> addPadding(List<String> list, int iteration) {
        var newList = new ArrayList<String>();

        var iterationSign = iteration % 2 == 1 ? ENHANCEMENT.charAt(0) : ENHANCEMENT.charAt(256);
        var colPadding = String.valueOf(iterationSign).concat(String.valueOf(iterationSign)).concat(String.valueOf(iterationSign));

        var sub = list.stream().map(s -> colPadding.concat(s).concat(colPadding))
                .collect(Collectors.toList());

        var chars = new char[sub.get(0).length()];
        Arrays.fill(chars, iterationSign);
        var rowPad = new String(chars);

        newList.add(rowPad);
        newList.add(rowPad);
        newList.add(rowPad);
        newList.addAll(sub);
        newList.add(rowPad);
        newList.add(rowPad);
        newList.add(rowPad);

        return newList;
    }

    public static int toIndex(String s) {
        var value = 0;
        for (var i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '#') {
                value += Math.pow(2, s.length() - 1 - i);
            }
        }
        return value;
    }
}