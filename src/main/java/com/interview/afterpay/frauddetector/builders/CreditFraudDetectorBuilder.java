package com.interview.afterpay.frauddetector.builders;


import com.interview.afterpay.frauddetector.BatchCreditFraudDetector;
import com.interview.afterpay.frauddetector.rules.FraudDetectionRule;
import com.interview.afterpay.entities.CreditRecord;

import java.util.List;
import java.util.Observer;

public class CreditFraudDetectorBuilder implements FraudDetectorBuilder<CreditRecord> {
    private BatchCreditFraudDetector detector;

    public CreditFraudDetectorBuilder() {
        this.reset();
    }

    private void reset() {
        this.detector = new BatchCreditFraudDetector();
    }

    @Override
    public CreditFraudDetectorBuilder registerRuleSet(List<FraudDetectionRule<CreditRecord>> rules) {
        for(FraudDetectionRule<CreditRecord> rule: rules) {
            this.detector.addRule(rule);
        }

        return this;
    }

    @Override
    public CreditFraudDetectorBuilder notifyOnSuccess(List<Observer> observers) {
        for (Observer observer: observers) {
            this.detector.registerOnSuccessObserver(observer);
        }

        return this;
    }

    @Override
    public CreditFraudDetectorBuilder notifyOnFailure(List<Observer> observers) {
        for (Observer observer: observers) {
            this.detector.registerOnFailureObserver(observer);
        }

        return this;
    }

    @Override
    public BatchCreditFraudDetector build() {
        return this.detector;
    }
}
