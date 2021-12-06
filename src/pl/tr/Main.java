package pl.tr;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private static final int MIN_INTERSECTIONS = 2;

    public static void main(String[] args) {
        long startTime = System.nanoTime();

        var lines = new ArrayList<Line>();

        try (var stream = Files.lines(Paths.get("input.txt"))) {
            stream.forEach(line -> {
                var points = Arrays.stream(line.split("->"))
                        .map(String::trim)
                        .flatMap(pair -> Arrays.stream(pair.split(","))
                                .map(String::trim)
                                .map(Integer::parseInt)
                        ).collect(Collectors.toList());
                lines.add(new Line(points.get(0), points.get(1), points.get(2), points.get(3)));
            });

            var countedIntersections = 0;

            for (var x = 0; x < 1000; x++) {
                for (var y = 0; y < 1000; y++) {
                    if (getIntersections(x, y, lines) >= MIN_INTERSECTIONS) {
                        countedIntersections++;
                    }
                }
            }

            System.out.println(countedIntersections);

        } catch (Exception e) {
            e.printStackTrace();
        }

        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.println("total time: " + totalTime);
    }

    private static long getIntersections(int x, int y, List<Line> lines) {
        return lines.stream()
                .filter(line -> line.x1 == line.x2 || line.y1 == line.y2)
                .filter(line -> line.passesThru(x, y)).count();
    }

    static class Line {
        private final int x1;
        private final int y1;
        private final int x2;
        private final int y2;

        Line(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        public boolean passesThru(int x, int y) {
            if (x1 == x2) {
                return x1 == x && y >= Math.min(y1, y2) && y <= Math.max(y1, y2);
            } else if (y1 == y2) {
                return y1 == y && x >= Math.min(x1, x2) && x <= Math.max(x1, x2);
            }

            int cx = x1, cy = y1;

            while(cy != y2 && cx != x2) {
                if(cx == x && cy == y) {
                    return true;
                }

                if(cy < y2) {
                    cy++;
                } else {
                    cy--;
                }

                if(cx < x2) {
                    cx++;
                } else {
                    cx--;
                }
            }

            return cx == x && cy == y;
        }
}

}
