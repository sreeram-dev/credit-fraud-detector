package com.interview.afterpay.entities;

import com.interview.afterpay.frauddetector.rules.FraudDetectionRule;

import java.util.*;

public class CreditFraudResult implements FraudResult<CreditRecord> {

    private Map<FraudDetectionRule, List<CreditRecord>> rawData;

    // empty constructor
    public CreditFraudResult() {
        this.rawData = new HashMap<>();
    }

    public CreditFraudResult(Map<FraudDetectionRule, List<CreditRecord>> rawData) {
        this.rawData = rawData;
    }

    @Override
    public List<CreditRecord> getAllRecords() {
        // a single record may fail multiple constraints
        // set returns an unordered list of items
        Set<CreditRecord> total = new HashSet<>();
        for (List<CreditRecord> fraudRecords: rawData.values()) {
            for (CreditRecord record: fraudRecords) {
                total.add(record);
            }
        }

        return new ArrayList<>(total);
    }

    @Override
    public List<FraudDetectionRule> getAllFailedConstraints() {
        List<FraudDetectionRule> rules = new ArrayList<>();

        for (FraudDetectionRule rule: rawData.keySet()) {
            rules.add(rule);
        }

        return rules;
    }

    public Map<FraudDetectionRule, List<CreditRecord>> getRawData() {
        return rawData;
    }

    public void setRawData(Map<FraudDetectionRule, List<CreditRecord>> rawData) {
        this.rawData = rawData;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\nFraud Result: \n");
        for (Map.Entry<FraudDetectionRule, List<CreditRecord>> kv: rawData.entrySet()) {
            builder.append("Constraint: " + kv.getKey().toString());
            builder.append("hashedIds which failed the Constraint: \n");
            for (String id: getDistinctIdsFromFailedRecords(getAllRecords())) {
                builder.append(id + "\n");
            }
        }

        return builder.toString();
    }

    @Override
    public void addRuleAndRecords(FraudDetectionRule rule, List<CreditRecord> records) {
        this.rawData.put(rule, records);
    }

    private Set<String> getDistinctIdsFromFailedRecords(List<CreditRecord> records) {
        Set<String> ids = new HashSet<>();
        for (CreditRecord record: records) {
            ids.add(record.getHashedCardId());
        }
        return ids;
    }

    /**
     * Get all the failed hashedids regardless of constraint
     * @return
     */
    public Set<String> getDistinctHashedIds() {
        Set<String> ids = new HashSet<>();
        for (CreditRecord record: getAllRecords()) {
            ids.add(record.getHashedCardId());
        }

        return ids;
    }
}
