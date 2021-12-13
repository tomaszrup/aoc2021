package pl.tr;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        try (var stream = Files.lines(Paths.get("input.txt"))) {

            var grid = new int[1500][1500];
            var folds = new ArrayList<String[]>();

            int[][] finalGrid = grid;
            stream.forEachOrdered(line -> {
                if(line.startsWith("fold")) {
                    folds.add(line.split(" ")[2].split("="));
                } else if (!line.isEmpty()){
                    var split = line.split(",");
                    finalGrid[Integer.parseInt(split[1])][Integer.parseInt(split[0])] = 1;
                }
            });


            for(var fold : folds) {
                var axis = fold[0];
                var val = Integer.parseInt(fold[1]);

                int[][] newGrid;

                if (axis.equals("y")) {
                    newGrid = new int[val][grid[0].length];

                    for(var row = 0; row < grid.length; row++) {
                        for(var col = 0; col < grid[0].length; col++) {
                            if(row < val) {
                                newGrid[row][col] = grid[row][col];
                            } else {
                                try { // xD
                                    newGrid[val - Math.abs(row - val)][col] = Math.max(newGrid[val - Math.abs(row - val)][col], grid[row][col]);
                                } catch (Exception e) {}
                            }
                        }
                    }

                } else {
                    newGrid = new int[grid.length][val];

                    for(var row = 0; row < grid.length; row++) {
                        for(var col = 0; col < grid[0].length; col++) {
                            if(col < val) {
                                newGrid[row][col] = grid[row][col];
                            } else {
                                try { // xD
                                    newGrid[row][val - Math.abs(col - val)] = Math.max(newGrid[row][val - Math.abs(col - val)], grid[row][col]);
                                } catch (Exception e) {}
                            }
                        }
                    }

                }

                grid = newGrid;

                System.out.print("fold number " + (folds.indexOf(fold) + 1) +": ");
                System.out.print(Arrays.stream(grid).map(c -> Arrays.stream(c).reduce(Integer::sum).getAsInt()).reduce(Integer::sum).get());
                System.out.println();
            }

            for(var row = 0; row < grid.length; row++) {
                for(var col = 0; col < grid[0].length; col++) {
                    if(grid[row][col] == 1) {
                        System.out.print("#");
                    } else {
                        System.out.print(".");
                    }
                }
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("total time (ms): " + totalTime);
    }


}
