package ai.prime.scenario.experimental.learning;

import ai.prime.knowledge.data.*;
import ai.prime.knowledge.data.base.VariableData;
import ai.prime.knowledge.nodes.confidence.Confidence;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class Learner {
    private final Data pattern;
    private final Set<Rule> leafs;
    private final Set<Rule> rules;
    private final Consumer<Data> addedRuleCallback;

    public Learner(Data pattern, Consumer<Data> addedRuleCallback) {
        this.pattern = pattern;
        this.leafs = new HashSet<>();
        this.rules = new HashSet<>();
        this.addedRuleCallback = addedRuleCallback;
    }

    private DataModifier getModifierFromConfidence(Confidence confidence) {
        return confidence.getStrength() > 0.0 ? DataModifier.POSITIVE : DataModifier.NEGATIVE;
    }

    private Expression getExpressionFromCondition(Data condition, Confidence confidence) {
        var modifier = getModifierFromConfidence(confidence);

        return new Expression(condition, modifier);
    }

    private Expression extractPatternFromCondition(Expression conditionExpression, Unification unification) {
        Map<Data, Expression> mapping = new HashMap<>();
        unification.getBoundedVariables().forEach(boundVariableName -> {
            var boundExpression = unification.getBound(boundVariableName);
            var boundVariable = new VariableData(boundVariableName);
            var boundVariableExpression = new Expression(boundVariable, boundExpression.getModifier());
            var boundData = boundExpression.getData();
            mapping.put(boundData, boundVariableExpression);
        });

        return conditionExpression.replace(mapping);
    }

    private Rule getRuleFromEvidence(Evidence evidence) {
        Unification unification = pattern.unify(evidence.data());
        Set<Expression> conditions = new HashSet<>();
        evidence.conditions().forEach((conditionData, confidence) -> {
            var conditionExpression = getExpressionFromCondition(conditionData, confidence);
            var patternCondition = extractPatternFromCondition(conditionExpression, unification);
            conditions.add(patternCondition);
        });
        var modifier = getModifierFromConfidence(evidence.confidence());

        return new Rule(modifier, conditions);
    }

    private Rule extractMatchFromSmaller(Rule smaller, Rule larger) {
        Set<Expression> conditions = new HashSet<>();
        smaller.conditions().forEach(condition -> {
           if (larger.conditions().contains(condition)) {
               conditions.add(condition);
           }
        });

        return new Rule(smaller.modifier(), conditions);
    }

    private Rule extractMatch(Rule rule1, Rule rule2) {
        return rule1.conditions().size() < rule2.conditions().size() ?
            extractMatchFromSmaller(rule1, rule2) :
            extractMatchFromSmaller(rule2, rule1);
    }

    private Rule extractDiff(Rule rule, Rule other) {
        Set<Expression> conditions = new HashSet<>();
        rule.conditions().forEach(condition -> {
            if (!other.conditions().contains(condition)) {
                conditions.add(condition);
            }
        });

        return new Rule(rule.modifier(), conditions);
    }

    private Data buildRuleData(Rule rule) {
        Expression target = new Expression(pattern, rule.modifier());
        Expression[] conditions = rule.conditions().toArray(new Expression[rule.conditions().size()]);

        Expression[] expressions = new Expression[rule.conditions().size() + 1];
        expressions[0] = target;
        System.arraycopy(conditions, 0, expressions, 1, conditions.length);
        Data ruleData = new Data(new DataType("rule"), expressions);

        return  ruleData;
    }

    private void reportNew(Rule match) {
        Data ruleData = buildRuleData(match);
        addedRuleCallback.accept(ruleData);
    }

    private void createMatches(Rule rule) {
        Set<Rule> matches = new HashSet<>();
        leafs.forEach(other -> {
            if (rule.modifier().equals(other.modifier())) {
                matches.add(extractMatch(rule, other));
            } else {
                matches.add(extractDiff(rule, other));
                matches.add(extractDiff(other, rule));
            }
        });

        matches.forEach(match -> {
            if (!rules.contains(match)) {
                  rules.add(match);
                  reportNew(match);
            }
        });
    }

    public void addEvidence(Evidence evidence) {
        Rule rule = getRuleFromEvidence(evidence);

        createMatches(rule);
        leafs.add(rule);
    }

    public Data getPattern() {
        return pattern;
    }
}
