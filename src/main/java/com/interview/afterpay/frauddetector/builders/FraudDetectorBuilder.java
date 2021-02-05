package com.interview.afterpay.frauddetector.builders;

import com.interview.afterpay.frauddetector.DetectorSpec;
import com.interview.afterpay.frauddetector.rules.FraudDetectionRule;

import java.util.List;
import java.util.Observer;

public interface FraudDetectorBuilder<T> {

    /**
     * Register the rules that will validate the dataset.
     * @param rules
     */
    FraudDetectorBuilder<T> registerRuleSet(List<FraudDetectionRule<T>> rules);

    /**
     * On Success, notify the registered observers about the job.
     * @param observers
     */
    FraudDetectorBuilder<T> notifyOnSuccess(List<Observer> observers);

    /**
     * On Failure, notify the registered the observers about the job.
     * @param observers
     */
    FraudDetectorBuilder<T> notifyOnFailure(List<Observer> observers);

    /**
     * Return the fraud detector with registered observers and ruleset.
     * @return
     */
    DetectorSpec<T> build();
}
