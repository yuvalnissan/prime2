package ai.prime.scenario.environment.towers;

import ai.prime.agent.Agent;
import ai.prime.agent.interaction.Actuator;
import ai.prime.common.utils.Logger;
import ai.prime.knowledge.data.Expression;
import ai.prime.scenario.Environment;

import java.util.*;
import java.util.stream.IntStream;

public abstract class Towers extends Environment {
    private static final int NUMBER_OF_TOWERS = 3;
    private static final int MID_TOWER = 2;
    private static final int INITIAL_TOWER = 1;

    private final int numberOfDisks;
    private Map<Integer, Stack<Integer>> towers;

    public Towers(int numberOfDisks) {
        this.numberOfDisks = numberOfDisks;
        this.innerReset();
    }

    @Override
    public Collection<Actuator> getActuators(Agent agent) {
        //TODO this is a temporary cheat until self awareness is detached from environments
        return new HashSet<>(List.of(new TowerActuator(agent, this)));
    }

    @Override
    public void innerReset() {
        resetTowers();
        resetDisks();
        print();
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

    private Set<Expression> getTowersState() {
        Set<Expression> expressions = new HashSet<>();
        IntStream.rangeClosed(1, NUMBER_OF_TOWERS).forEach(towerId -> {
            expressions.add(Expression.fromString(String.format("rel(isa,t%d,tower)", towerId)));
            Expression isEmpty = Expression.fromString(String.format("rel(isa,t%d,empty)", towerId));
            if (isTowerEmpty(towerId)) {
                expressions.add(isEmpty);
            } else {
                expressions.add(isEmpty.not());
                getTowerTopDisk(towerId).ifPresent(topDisk -> expressions.add(Expression.fromString(String.format("rel(top,t%d,d%d)", towerId, topDisk))));
                getTowerDisks(towerId).forEach(diskId -> {
                    expressions.add(Expression.fromString(String.format("rel(on,d%d,t%d)", diskId, towerId)));
                    expressions.add(Expression.fromString(String.format("rel(size,d%d,%d)", diskId, diskId)));
                    expressions.add(Expression.fromString(String.format("rel(isa,d%d,disk)", diskId)));
                });
            }
        });

        return expressions;
    }

    private Set<Expression> getGameState() {
        Set<Expression> expressions = new HashSet<>();
        if (isWon()) {
            expressions.add(Expression.fromString("won"));
        } else {
            expressions.add(Expression.fromString("not(won)"));
        }

        return expressions;
    }

    @Override
    protected Set<Expression> getState() {
        Set<Expression> expressions = new HashSet<>();
        expressions.addAll(getTowersState());
        expressions.addAll(getGameState());

        return expressions;
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

        print();

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
