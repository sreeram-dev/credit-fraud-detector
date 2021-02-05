package com.interview.afterpay.cfd.entities;

import com.interview.afterpay.cfd.frauddetector.rules.FraudDetectionRule;

import java.io.Serializable;
import java.util.*;

public class CreditFraudResult implements FraudResult<CreditRecord>, Serializable {

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
        Set<CreditRecord> total = new HashSet();
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
            builder.append("Number of hashedIds for Constraint: \n");
            for (CreditRecord record: getAllRecords()) {
                builder.append(record.getHashedCardId() + "\n");
            }
        }

        return builder.toString();
    }

    @Override
    public void addRuleAndRecords(FraudDetectionRule rule, List<CreditRecord> records) {
        this.rawData.put(rule, records);
    }
}
