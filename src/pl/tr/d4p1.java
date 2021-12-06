package pl.tr;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        try (var stream = Files.lines(Paths.get("input.txt"))) {

            var boards = new ArrayList<Board>();
            var data = stream.collect(Collectors.toList());
            var boardsData = data.subList(1, data.size() - 1);
            boardsData.forEach(line -> {
                if (line.isEmpty()) {
                    boards.add(new Board());
                } else {
                    var lastBoard = boards.get(boards.size() - 1);
                    lastBoard.addRow(line);
                }
            });

            var numbersToDraw = Arrays.stream(data.get(0).split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            var boardsDrawOrder = new ArrayList<Board>();

            for(var number : numbersToDraw) {
                for(var board : boards) {
                    if(board.checkNumber(number)) {
                        System.out.println(board.getUncheckedSum() * number);
                        return;
                    }
                }
            }

        } catch (Exception e) {
             e.printStackTrace();
        }
        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.println(totalTime / 1000000 + " ns");
    }

    static class Board {
        private final List<List<Integer>> numMatrix = new ArrayList<>();

        public List<List<Integer>> getNumMatrix() {
            return numMatrix;
        }

        public void addRow(String row) {
            var integerRow = Arrays.stream(row.split(" "))
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            numMatrix.add(integerRow);
        }

        public int getUncheckedSum() {
            var sum = 0;
            for(var row = 0; row < numMatrix.size(); row++) {
                for(var col = 0; col < numMatrix.get(0).size(); col++) {
                    var num = numMatrix.get(row).get(col);
                    if(num != -1) {
                        sum += num;
                    }
                }
            }
            return sum;
        }

        public boolean checkNumber(Integer num) {
            for(var row = 0; row < numMatrix.size(); row++) {
                for(var col = 0; col < numMatrix.get(0).size(); col++) {
                    if(num == numMatrix.get(row).get(col)) {
                        numMatrix.get(row).set(col, -1);
                    }
                }
            }

            for(var row = 0; row < numMatrix.size(); row++) {
                if(numMatrix.get(row).stream().filter(n -> n == -1).count() == numMatrix.size()) {
                    return true;
                }
            }

            for(var col = 0; col < numMatrix.get(0).size(); col++) {
                var list = new ArrayList<Integer>();

                for (List<Integer> matrix : numMatrix) {
                    list.add(matrix.get(col));
                }

                if(list.stream().filter(n -> n == -1).count() == numMatrix.get(0).size()) {
                    return true;
                }
            }

            return false;
        }
    }

}
