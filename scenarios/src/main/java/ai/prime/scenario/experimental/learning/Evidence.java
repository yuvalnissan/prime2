package ai.prime.scenario.experimental.learning;

import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.nodes.confidence.Confidence;

import java.util.Map;

public record Evidence(Data data, Confidence confidence, Map<Data, Confidence> conditions) {}
