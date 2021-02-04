package com.interview.afterpay.cfd.frauddetectors.builders;


import com.interview.afterpay.cfd.frauddetectors.BatchCreditFraudDetector;
import com.interview.afterpay.cfd.frauddetectors.DetectorSpec;
import com.interview.afterpay.cfd.frauddetectors.rules.FraudDetectionRule;
import com.interview.afterpay.cfd.record.CreditRecord;

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
    public void registerRuleSet(List<FraudDetectionRule> rules) {
        for(FraudDetectionRule rule: rules) {
            this.detector.addRule(rule);
        }
    }

    @Override
    public void notifyOnSuccess(List<Observer> observers) {
        for (Observer observer: observers) {
            this.detector.registerOnSuccessObserver(observer);
        }
    }

    @Override
    public void notifyOnFailure(List<Observer> observers) {
        for (Observer observer: observers) {
            this.detector.registerOnFailureObserver(observer);
        }
    }

    @Override
    public DetectorSpec<CreditRecord> build() {
        return this.detector;
    }
}
