package com.interview.afterpay.cfd.frauddetectors.builders;

import com.interview.afterpay.cfd.frauddetectors.DetectorSpec;
import com.interview.afterpay.cfd.frauddetectors.rules.FraudDetectionRule;

import java.util.List;
import java.util.Observer;

public interface FraudDetectorBuilder<T> {
    void registerRuleSet(List<FraudDetectionRule> rules);
    void notifyOnSuccess(List<Observer> observers);
    void notifyOnFailure(List<Observer> observers);
    DetectorSpec<T> build();
}
