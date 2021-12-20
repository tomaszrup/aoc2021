package pl.tr;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] ars) {

        var scanners = new ArrayList<Scanner>();
        var index = new AtomicInteger();

        try (var stream = Files.lines(Paths.get("input.txt"))) {
            stream.forEachOrdered(line -> {
                if (!line.isEmpty()) {
                    if (line.startsWith("---")) {
                        scanners.add(new Scanner(index.getAndIncrement()));
                    } else {
                        var currentScanner = scanners.get(scanners.size() - 1);
                        var split = Arrays.stream(line.split(","))
                                .map(Integer::parseInt)
                                .collect(Collectors.toList());
                        var point = new Point(split.get(0), split.get(1), split.get(2));
                        currentScanner.beacons.add(point);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        scanners.forEach(Scanner::calcAbsDifferences);

        var matchedScanners = new ArrayList<Integer>();
        var scannerDistances = new HashMap<Integer, Point>();
        scannerDistances.put(0, new Point(0, 0, 0));

        while (matchedScanners.size() != scanners.size() - 1) {
            for (var scIndex = 1; scIndex < scanners.size(); scIndex++) {
                if (matchedScanners.contains(scIndex)) {
                    continue;
                }

                var scanner0 = scanners.get(0);
                var scannerCurrent = scanners.get(scIndex);

                var overlappingDistances = scanner0.distances.entrySet()
                        .stream().flatMap(e1 -> {
                            var match = scannerCurrent.distances.entrySet().stream().filter(e2 -> {
                                var l = new ArrayList<>(e2.getValue().toList());
                                l.addAll(e1.getValue().toList());
                                return l.stream().distinct().count() == 3;
                            }).findFirst();

                            return match.map(stringPointEntry -> Optional.of(e1.getKey() + "=" + stringPointEntry.getKey())
                                            .stream())
                                    .orElseGet(() -> Optional.<String>empty().stream());
                        }).collect(Collectors.toList());

                var possibleMatches = new HashMap<String, List<String>>();

                overlappingDistances.forEach(pair -> {
                    var split = pair.split("=");

                    var sc1points = split[0].split("\\|")[1].split(",");
                    var sc2points = split[1].split("\\|")[1].split(",");


                    possibleMatches.put(sc1points[0],
                            concatList(possibleMatches.getOrDefault(sc1points[0], Collections.emptyList()), List.of(sc2points)));
                    possibleMatches.put(sc1points[1],
                            concatList(possibleMatches.getOrDefault(sc1points[1], Collections.emptyList()), List.of(sc2points)));
                });

                var matches = possibleMatches.entrySet()
                        .stream()
                        .filter(e -> e.getValue().size() > 2)
                        .map(e -> e.getKey() + "=" + mostCommon(e.getValue()))
                        .collect(Collectors.toList());

                if (matches.size() >= 12) {
                    matchedScanners.add(scIndex);
                    Point distScanner0, distScannerCurrent;

                    do {
                        var firstMatch = matches.get(0).split("=");
                        var secondMatch = matches.get(1).split("=");

                        var sc0beacon1 = scanner0.beacons.get(Integer.parseInt(firstMatch[0]));
                        var sc0beacon2 = scanner0.beacons.get(Integer.parseInt(secondMatch[0]));

                        var scCurrentBeacon2 = scannerCurrent.beacons.get(Integer.parseInt(secondMatch[1]));
                        var scCurrentBeacon1 = scannerCurrent.beacons.get(Integer.parseInt(firstMatch[1]));

                        distScanner0 = sc0beacon1.dist(sc0beacon2);
                        distScannerCurrent = scCurrentBeacon1.dist(scCurrentBeacon2);
                    } while (distScanner0.toList().stream().map(Math::abs).distinct().count() != 3
                            && distScannerCurrent.toList().stream().map(Math::abs).distinct().count() != 3);

                    var rotation = distScanner0.getRotation(distScannerCurrent);
                    var rotatedBeacons = scannerCurrent.beacons.stream().map(b -> b.rotate(rotation))
                            .collect(Collectors.toList());
                    var firstMatch = matches.get(0).split("=");
                    var pointsDist = scanner0.beacons.get(Integer.parseInt(firstMatch[0]))
                            .dist(rotatedBeacons.get(Integer.parseInt(firstMatch[1])));

                    scannerDistances.put(scIndex, pointsDist);

                    scanner0.beacons.addAll(rotatedBeacons.stream().map(b -> b.add(pointsDist)).collect(Collectors.toList()));
                    scanner0.beacons = scanner0.beacons.stream().distinct().collect(Collectors.toList());
                    scanner0.calcAbsDifferences();

                    System.out.println("matched scanner: " + scIndex + "(" + matchedScanners.size() + "/29)");
                }
            }
        }

        var scDistances = scannerDistances.values().stream().collect(Collectors.toList());

        var manDist = Integer.MIN_VALUE;

        for(var i = 0; i < scDistances.size(); i++) {
            for(var j = 0; j < scDistances.size(); j++) {
                if(i != j) {

                    var sc1 = scDistances.get(i);
                    var sc2 = scDistances.get(j);

                    var dist = Math.abs(sc1.x-sc2.x) + Math.abs(sc1.y-sc2.y) + Math.abs(sc1.z-sc2.z);
                    if(dist > manDist) manDist = dist;
                }
            }
        }

        System.out.println(manDist);
    }

    public static class Scanner {
        private final int index;
        private List<Point> beacons = new ArrayList<>();
        private final Map<String, Point> distances = new HashMap<>();

        public Scanner(int index) {
            this.index = index;
        }

        public void calcAbsDifferences() {
            for (var i = 0; i < beacons.size(); i++) {
                for (var j = 0; j < beacons.size(); j++) {
                    var point1 = beacons.get(i);
                    var point2 = beacons.get(j);
                    if (point1 != point2) {
                        var diff = point1.distAbs(point2);
                        if (!distances.containsValue(diff)) {
                            distances.put(index + "|" + i + "," + j, diff);
                        }
                    }
                }
            }
        }
    }

    public static class Point {
        private final int x;
        private final int y;
        private final int z;

        public Point(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            var point = (Point) o;
            return x == point.x && y == point.y && z == point.z;
        }

        public List<Integer> toList() {
            return List.of(x, y, z);
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z);
        }

        public Point distAbs(Point point) {
            var x = Math.abs(this.x - point.x);
            var y = Math.abs(this.y - point.y);
            var z = Math.abs(this.z - point.z);
            return new Point(x, y, z);
        }

        public Point add(Point point) {
            var xx = x + point.x;
            var yy = y + point.y;
            var zz = z + point.z;
            return new Point(xx, yy, zz);
        }

        public Point dist(Point point) {
            var x = this.x - point.x;
            var y = this.y - point.y;
            var z = this.z - point.z;
            return new Point(x, y, z);
        }

        public Rotation getRotation(Point point) {
            var xRot = x == point.x ? "x:1" :
                    (x == -point.x ? "x:-1"
                            : x == point.y ? "y:1"
                            : (x == -point.y ? "y:-1"
                            : (x == point.z ? "z:1" : "z:-1")));
            var yRot = y == point.x ? "x:1" :
                    (y == -point.x ? "x:-1"
                            : y == point.y ? "y:1"
                            : (y == -point.y ? "y:-1"
                            : (y == point.z ? "z:1" : "z:-1")));
            var zRot = z == point.x ? "x:1" :
                    (z == -point.x ? "x:-1"
                            : z == point.y ? "y:1"
                            : (z == -point.y ? "y:-1"
                            : (z == point.z ? "z:1" : "z:-1")));
            return new Rotation(xRot, yRot, zRot);
        }

        public Point rotate(Rotation rotation) {
            var x = rotation.x.split(":");
            var y = rotation.y.split(":");
            var z = rotation.z.split(":");

            try {
                return new Point(
                        (int) this.getClass().getDeclaredField(x[0]).get(this) * Integer.parseInt(x[1]),
                        (int) this.getClass().getDeclaredField(y[0]).get(this) * Integer.parseInt(y[1]),
                        (int) this.getClass().getDeclaredField(z[0]).get(this) * Integer.parseInt(z[1])
                );
            } catch (Exception e) {
                throw new RuntimeException("XD");
            }
        }

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    ", z=" + z +
                    '}';
        }
    }

    public static <T> List<T> concatList(List<T>... lists) {
        List<T> result = new ArrayList<>();

        for (List<T> l : lists) {
            result.addAll(l);
        }

        return result;
    }

    public static <T> T mostCommon(List<T> list) {
        var map = new HashMap<T, Integer>();

        for (var t : list) {
            var val = map.get(t);
            map.put(t, val == null ? 1 : val + 1);
        }

        Map.Entry<T, Integer> max = null;

        for (var e : map.entrySet()) {
            if (max == null || e.getValue() > max.getValue())
                max = e;
        }

        return max.getKey();
    }

    public static class Rotation {
        private final String x;
        private final String y;
        private final String z;

        public Rotation(String x, String y, String z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}