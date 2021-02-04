package com.interview.afterpay.cfd.frauddetector.builders;

import com.interview.afterpay.cfd.frauddetector.DetectorSpec;
import com.interview.afterpay.cfd.frauddetector.rules.FraudDetectionRule;

import java.util.List;
import java.util.Observer;

public interface FraudDetectorBuilder<T> {

    /**
     * Register the rules that will validate the dataset.
     * @param rules
     */
    FraudDetectorBuilder registerRuleSet(List<FraudDetectionRule> rules);

    /**
     * On Success, notify the registered observers about the job.
     * @param observers
     */
    FraudDetectorBuilder notifyOnSuccess(List<Observer> observers);

    /**
     * On Failure, notify the registered the observers about the job.
     * @param observers
     */
    FraudDetectorBuilder notifyOnFailure(List<Observer> observers);

    /**
     * Return the fraud detector with registered observers and ruleset.
     * @return
     */
    DetectorSpec<T> build();
}
