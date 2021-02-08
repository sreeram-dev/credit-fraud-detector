package com.interview.afterpay.frauddetector;

import com.interview.afterpay.frauddetector.rules.FraudDetectionRule;
import com.interview.afterpay.entities.FraudResult;

import java.util.List;
import java.util.Observer;

/**
 * Generic Interface to detect fraud across credit and debit records
 * @param <T>
 */
public interface DetectorSpec<T> {

    /**
     * Add a rule or constraint that the detector has to evaluate against
     * @param rule
     */
    void addRule(FraudDetectionRule<T> rule);

    FraudResult<T> detectAndGetFraudulentRecords(List<T> dataSet);

    /**
     * Notify observers if the detector has executed successfully
     * @param observer
     */
    void registerOnSuccessObserver(Observer observer);

    /**
     * Notify observers if the detector has not executed successfully
     * @param observer
     */
    void registerOnFailureObserver(Observer observer);
}
