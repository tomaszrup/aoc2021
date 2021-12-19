package pl.tr;

import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] ars) {
        var startTime = System.currentTimeMillis();

        var snailStack = new Stack<SnailfishNumber>();

        try (var stream = Files.lines(Paths.get("input.txt"))) {
            var stack = new Stack<Character>();

            stream.forEachOrdered(line -> {
                line.replaceAll("\\s+", "").chars().boxed()
                        .map(c -> (char) c.intValue())
                        .forEachOrdered(c -> {
                            if (c == '[') {
                                stack.add(c);
                            } else if (c == ']') {
                                var rightVal = stack.pop();
                                var leftVal = stack.pop();

                                if (Character.isDigit(rightVal)) {
                                    if (Character.isDigit(leftVal)) {
                                        snailStack.add(new SnailfishNumber
                                                (Character.getNumericValue(leftVal), Character.getNumericValue(rightVal)));
                                    } else {
                                        snailStack.add(new SnailfishNumber
                                                (snailStack.pop(), Character.getNumericValue(rightVal)));
                                    }
                                } else {
                                    if (Character.isDigit(leftVal)) {
                                        snailStack.add(new SnailfishNumber
                                                (Character.getNumericValue(leftVal), snailStack.pop()));
                                    } else {
                                        var pop1 = snailStack.pop();
                                        var pop2 = snailStack.pop();
                                        snailStack.add(new SnailfishNumber(pop2, pop1));
                                    }
                                }
                            } else if (c != ',') {
                                stack.add(c);
                            }
                        });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        var snailList = snailStack.stream().collect(Collectors.toList());
        var snailList2 = snailList.stream().map(c -> c.copy()).collect(Collectors.toList());

        var maxMag = Long.MIN_VALUE;

        for(var i = 0; i < snailList2.size(); i++) {
            for(var j = 0; j < snailList2.size() - 1; j++) {
                var left = snailList2.get(i);
                var right = snailList2.get(j);
                if(left != right) {
                    var created = new SnailfishNumber(left, right);
                    created.reduce();
                    if(created.getMagnitude() > maxMag) {
                        maxMag = created.getMagnitude();
                    }
                }
                snailList2 = snailList.stream().map(c -> c.copy()).collect(Collectors.toList());
            }

        }

        while (snailList.size() > 1) {
            var left = snailList.get(0);
            var right = snailList.get(1);

            var toAdd = new SnailfishNumber(left, right);
            toAdd.reduce();

            var newSnailList = new ArrayList<>(List.of(toAdd));
            newSnailList.addAll(snailList.subList(2, snailList.size()));
            snailList = newSnailList;
        }

        var num = snailList.get(0);

        System.out.println(num.getMagnitude());
        System.out.println(maxMag);

        var endTime = System.currentTimeMillis();
        var totalTime = endTime - startTime;
        System.out.println("total time (ms): " + totalTime);
    }

    public static class SnailfishNumber {
        private Object left;
        private Object right;
        private SnailfishNumber parent;

        public SnailfishNumber(Object left, Object right) {
            this.left = left;
            this.right = right;
            if (left instanceof SnailfishNumber) {
                ((SnailfishNumber) left).setParent(this);
            }
            if (right instanceof SnailfishNumber) {
                ((SnailfishNumber) right).setParent(this);
            }
        }

        public SnailfishNumber setParent(SnailfishNumber parent) {
            this.parent = parent;
            return this;
        }

        private int getDepth() {
            if (parent != null) {
                return 1 + parent.getDepth();
            }
            return 0;
        }


        private List<SnailfishNumber> scanForExplodes() {
            if (getDepth() >= 4) {
                return new ArrayList<>(List.of(this));
            } else {
                var explodes = new ArrayList<SnailfishNumber>();
                if (left instanceof SnailfishNumber) {
                    explodes.addAll(((SnailfishNumber) left).scanForExplodes());
                }
                if (right instanceof SnailfishNumber) {
                    explodes.addAll(((SnailfishNumber) right).scanForExplodes());
                }
                return explodes;
            }
        }

        private List<SnailfishNumber> scanForSplits() {
            var splits = new ArrayList<SnailfishNumber>();

            if (left instanceof SnailfishNumber) {
                splits.addAll(((SnailfishNumber) left).scanForSplits());
            } else if (left instanceof Integer && (Integer) left >= 10) {
                splits.add(this);
            }

            if (right instanceof SnailfishNumber) {
                splits.addAll(((SnailfishNumber) right).scanForSplits());
            } else if (right instanceof Integer && (Integer) right >= 10) {
                splits.add(this);
            }

            return splits;
        }

        public boolean reduce() {
            if (getDepth() == 0) {
                var x = this;
                List<SnailfishNumber> explodes, splits;
                do {
                    explodes = scanForExplodes();
                    explodes.forEach(num -> {
                        var a = x;
                        num.explode();
                    });
                    splits = scanForSplits();
                    splits.stream().findFirst().ifPresent(SnailfishNumber::split);
                } while (!explodes.isEmpty() || !splits.isEmpty());
            }
            return false;
        }

        public void split() {
            if (left instanceof Integer && (Integer) left >= 10) {
                splitLeft();
            } else if (right instanceof Integer && (Integer) right >= 10) {
                splitRight();
            }
        }

        public void splitLeft() {
            var ll = (Integer) (int) Math.floor((double) (Integer) left / 2);
            var rr = (Integer) (int) Math.ceil((double) (Integer) left / 2);
            left = new SnailfishNumber(ll, rr).setParent(this);
        }

        public void splitRight() {
            var ll = (Integer) (int) Math.floor((double) (Integer) right / 2);
            var rr = (Integer) (int) Math.ceil((double) (Integer) right / 2);
            right = new SnailfishNumber(ll, rr).setParent(this);
        }

        public void explode() {
            addToLeft((Integer) left);
            addToRight((Integer) right);

            if (this.parent != null) {
                if (this == parent.left) {
                    parent.left = 0;
                } else {
                    parent.right = 0;
                }
            }

        }

        public SnailfishNumber addToLeft(Integer num) {
            if (num == 0) {
                return null;
            }

            try {
                var current = this;

                while (current == current.parent.left) {
                    current = current.parent;
                }

                var found = current.parent.left;

                if (found instanceof Integer) {
                    current.parent.left = (Integer) current.parent.left + num;

                    System.out.println(num + " -> [" + current.parent.left + "," + current.parent.left + "] left explode");

                    return current.parent;
                } else {
                    while (((SnailfishNumber) found).right instanceof SnailfishNumber) {
                        found = ((SnailfishNumber) found).right;
                    }
                    ((SnailfishNumber) found).right = (Integer) ((SnailfishNumber) found).right + num;

                    System.out.println("[" + ((SnailfishNumber) found).left + "," + ((SnailfishNumber) found).right + "] <- " + num + " left explode");
                    return (SnailfishNumber) found;
                }
            } catch (Exception e) {
                System.out.println(num + " missed from left explode");
                return null;
            }
        }

        public SnailfishNumber addToRight(Integer num) {
            if (num == 0) {
                return null;
            }

            try {
                var current = this;

                while (current == current.parent.right) {
                    current = current.parent;
                }

                var found = current.parent.right;

                if (found instanceof Integer) {
                    current.parent.right = (Integer) current.parent.right + num;
                    System.out.println("[" + current.parent.left + "," + current.parent.right + "] <- " + num + " right explode");

                    return current.parent;
                } else {
                    while (((SnailfishNumber) found).left instanceof SnailfishNumber) {
                        found = ((SnailfishNumber) found).left;
                    }

                    ((SnailfishNumber) found).left = (Integer) ((SnailfishNumber) found).left + num;
                    System.out.println(num + " -> [" + ((SnailfishNumber) found).left + "," + ((SnailfishNumber) found).right + "] right explode");
                    return (SnailfishNumber) found;
                }
            } catch (Exception e) {
                System.out.println(num + " missed from right explode");
                return null;
            }
        }

        public long getMagnitude() {
            if (left instanceof Integer && right instanceof Integer) {
                return (3L * (Integer) left) + (2L * (Integer) right);
            }
            else if (left instanceof Integer && right instanceof SnailfishNumber) {
                return (3L * (Integer) left) + (2L * ((SnailfishNumber) right).getMagnitude());
            }
            else if (left instanceof SnailfishNumber && right instanceof Integer) {
                return (3L * ((SnailfishNumber) left).getMagnitude()) + 2 * (Integer) right;
            } else {
                return (3 * ((SnailfishNumber) left).getMagnitude()) + (2 * ((SnailfishNumber) right).getMagnitude());
            }
        }

        @Override
        public String toString() {
            var s = "";
            if(left instanceof Integer) {
                s = s.concat("[").concat(String.valueOf(left)).concat(",");
            } else {
                s = s.concat("[").concat(left.toString()).concat(",");
            }
            if(right instanceof Integer) {
                s = s.concat(String.valueOf(right)).concat("]");
            } else {
                s = s.concat(right.toString()).concat("]");
            }

            return s;
        }

        public SnailfishNumber copy() {
            var l = left instanceof SnailfishNumber ? ((SnailfishNumber) left).copy() : left;
            var r = right instanceof SnailfishNumber ? ((SnailfishNumber) right).copy() : right;
            return new SnailfishNumber(l, r);
        }
    }

}