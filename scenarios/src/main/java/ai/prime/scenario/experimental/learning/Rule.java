package ai.prime.scenario.experimental.learning;

import ai.prime.knowledge.data.DataModifier;
import ai.prime.knowledge.data.Expression;

import java.util.Set;

public record Rule(DataModifier modifier, Set<Expression> conditions) {}
