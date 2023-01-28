package ai.prime.scenario.environment.towers;

import ai.prime.common.utils.Logger;
import ai.prime.scenario.Environment;

import java.util.*;
import java.util.stream.IntStream;

public class Towers extends Environment {
    private static final int NUMBER_OF_TOWERS = 3;
    private static final int MID_TOWER = 2;
    private static final int INITIAL_TOWER = 1;

    private final int numberOfDisks;
    private Map<Integer, Stack<Integer>> towers;

    public Towers(int numberOfDisks) {
        this.numberOfDisks = numberOfDisks;
        this.reset();
    }

    @Override
    public void reset() {
        resetTowers();
        resetDisks();
    }

    private void resetDisks() {
        Stack<Integer> firstTower = towers.get(INITIAL_TOWER);
        IntStream
                .iterate(numberOfDisks, i -> i - 1)
                .limit(numberOfDisks)
                .forEach(firstTower::push);
    }

    private void resetTowers() {
        towers = new HashMap<>();
        IntStream.rangeClosed(1, NUMBER_OF_TOWERS).forEach(i -> towers.put(i, new Stack<>()));
    }

    public int getNumberOfDisksOnTower(int towerId) {
        Stack<Integer> tower = towers.get(towerId);

        return tower.size();
    }

    public boolean move(int from, int to) {
        if (!canMove(from, to)) {
            Logger.error("Illegal move: " + from + " to " + to);
            return false;
        }

        Stack<Integer> fromTower = towers.get(from);
        Stack<Integer> toTower = towers.get(to);

        int value = fromTower.pop();
        toTower.push(value);

        return true;
    }

    public boolean isTowerEmpty(int towerId) {
        Stack<Integer> tower = towers.get(towerId);
        return tower.empty();
    }

    public Optional<Integer> getTowerTopDisk(int towerId) {
        Stack<Integer> tower = towers.get(towerId);

        Integer top = null;
        if (!tower.empty()) {
            top = tower.peek();
        }

        return Optional.ofNullable(top);
    }

    public List<Integer> getTowerDisks(int towerId) {
        return towers.get(towerId);
    }

    private boolean canMove(int from, int to) {
        if (from < 1 || from > NUMBER_OF_TOWERS) {
            throw new RuntimeException(from + " is not a tower number");
        }

        if (to < 1 || to > NUMBER_OF_TOWERS) {
            throw new RuntimeException(to + " is not a tower number");
        }

        Stack<Integer> fromTower = towers.get(from);
        Stack<Integer> toTower = towers.get(to);

        if (fromTower.empty()) {
            return false;
        }

        if (toTower.empty()) {
            return true;
        }

        int fromValue = fromTower.peek();
        int toValue = toTower.peek();

        return fromValue < toValue;
    }

    public boolean isWon() {
        if (!towers.get(INITIAL_TOWER).empty()) {
            return false;
        }

        if (!towers.get(MID_TOWER).empty()) {
            return false;
        }

        return true;
    }

    public void print() {
        towers.keySet().forEach(i -> {
            Stack<Integer> tower = towers.get(i);
            System.out.println(i + ": " + tower);
        });

        System.out.println("------------------------------");
    }
}
