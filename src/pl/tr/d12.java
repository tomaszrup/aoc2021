package pl.tr;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    private static final String START = "start";
    private static final String END = "end";

    private static final List<String> PATHS = new ArrayList<>();

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        try (var stream = Files.lines(Paths.get("input.txt"))) {
            var connections = new ArrayList<Pair>();

            stream.forEach(str -> {
                var split = str.split("-");
                connections.add(new Pair(split[0], split[1]));
            });

            traverse(START, connections, "");

            PATHS.forEach(System.out::println);
            System.out.println((int) PATHS.stream().distinct().count());
        } catch (Exception e) {
            e.printStackTrace();
        }


        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("total time (ms): " + totalTime);
    }


    private static void traverse(String cave, List<Pair> connections, String visited) {
        if (cave.equals(END)) {
            PATHS.add(visited.concat(END));
        } else {
            var conns = findAllConnected(cave, connections);
            for (var c : conns) {
                if (c.equals(START)) {
                    continue;
                }
                if(Character.isUpperCase(c.toCharArray()[0])) {
                    traverse(c, connections, visited.concat(cave + ","));
                } else {
                    if(!visited.contains(c)) {
                        traverse(c, connections, visited.concat(cave + ","));
                    } else {
                        var cavesVisited2Times = Arrays.stream(visited.concat(cave).split(","))
                                .filter(s -> !s.isEmpty())
                                .filter(s -> !Character.isUpperCase(s.toCharArray()[0]))
                                .collect(Collectors.groupingBy(Function.identity(),
                                        Collectors.counting()))
                                .entrySet()
                                .stream().filter(e -> e.getValue() > 1)
                                .count();

                        if(cavesVisited2Times == 0) {
                            traverse(c, connections, visited.concat(cave + ","));
                        }
                    }
                }
            }
        }
    }

    private static Set<String> findAllConnected(String cave, List<Pair> connections) {
        var c1 = connections.stream().filter(e -> e.getL().equals(cave)).map(Pair::getR);
        var c2 = connections.stream().filter(e -> e.getR().equals(cave)).map(Pair::getL);
        return Stream.concat(c1, c2).collect(Collectors.toSet());
    }

    private static final class Pair {
        private final String l;
        private final String r;

        private Pair(String l, String r) {
            this.l = l;
            this.r = r;
        }

        public String getR() {
            return r;
        }

        public String getL() {
            return l;
        }
    }


}
