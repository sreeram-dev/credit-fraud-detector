package com.interview.afterpay.cfd.frauddetector;

import com.interview.afterpay.cfd.frauddetector.rules.FraudDetectionRule;
import com.interview.afterpay.cfd.entities.FraudResult;

import java.util.List;
import java.util.Observer;

/**
 * Generic Interface to detect fraud across credit and debit records
 * @param <T>
 */
public interface DetectorSpec<T> {
    void addRule(FraudDetectionRule rule);

    FraudResult detectAndGetFraudulentRecords(List<T> dataSet);

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
