package pl.tr;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class Main {
    private static final Map<Character, Integer> COST = Map.of(
            ')', 3,
            ']', 57,
            '}', 1197,
            '>', 25137
    );

    private static final Map<Character, Integer> CLOSER_SCORE = Map.of(
            ')', 1,
            ']', 2,
            '}', 3,
            '>', 4
    );

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        try (var stream = Files.lines(Paths.get("input.txt"))) {
            var score = stream.map(line -> {
                        var stack = new Stack<Character>();
                        var amogus = line.chars().boxed().map(i -> (char) i.intValue())
                                .collect(Collectors.toList());

                        for(var c : amogus) {
                            if(isOpener(c)) {
                                stack.push(c);
                            } else {
                                var lastOpener = stack.pop();
                                if(!closes(c, lastOpener)) {
                                    return 0L;
                                }
                            }
                        }
                        var subScore = new AtomicLong();
                        var l = new ArrayList<>(stack);
                        Collections.reverse(l);

                        l.forEach(opener -> subScore.set(subScore.get() * 5 + CLOSER_SCORE.get(findCloser(opener))));
                        return subScore.get();
                    }).filter(c -> c != 0)
                    .sorted()
                    .collect(Collectors.toList());

            System.out.println(score);
            System.out.println(score.get((score.size()/2)));
        } catch (Exception e) {
            e.printStackTrace();
        }


        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("total time (ms): " + totalTime);
    }

    private static boolean isOpener(Character c) {
        return List.of('[', '(', '{', '<').contains(c);
    }

    private static char findCloser(char c) {
        switch (c) {
            case '[': return ']';
            case '(': return ')';
            case '<': return '>';
            case '{': return '}';
            default: return 'ඞ';
        }
    }

    private static boolean closes(char c, char lastOpener) {
        return c == findCloser(lastOpener);
    }
}
