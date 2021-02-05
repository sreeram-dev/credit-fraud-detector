package com.interview.afterpay.cfd.entities;

import com.interview.afterpay.cfd.frauddetector.rules.FraudDetectionRule;

import java.util.List;

public interface FraudResult<T> {

    List<T> getAllRecords();

    List<FraudDetectionRule> getAllFailedConstraints();

    void addRuleAndRecords(FraudDetectionRule rule, List<T> records);
}
