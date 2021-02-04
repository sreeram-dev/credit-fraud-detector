package com.interview.afterpay.cfd.frauddetector;

import com.interview.afterpay.cfd.frauddetector.rules.FraudDetectionRule;
import com.interview.afterpay.cfd.record.CreditRecord;
import com.interview.afterpay.cfd.result.CreditFraudResult;
import com.interview.afterpay.cfd.result.FraudResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observer;


/**
 * Fraud detector that processes each record in a batch file and
 * emits fraudulent records mapped to the ruleSet that have failed against.
 *
 * If the
 */
public class BatchCreditFraudDetector implements DetectorSpec<CreditRecord> {

    private final List<FraudDetectionRule> rules = new ArrayList<>();

    private final List<Observer> onSuccessObservers = new ArrayList<>();
    private final List<Observer> onFailureObservers = new ArrayList<>();

    private final List<CreditRecord> fraudulentRecords = new ArrayList<>();

    @Override
    public void addRule(FraudDetectionRule rule) {
        this.rules.add(rule);
    }

    @Override
    public FraudResult detectAndGetFraudulentRecords(List<CreditRecord> dataSet) {

        HashMap<FraudDetectionRule, List<CreditRecord>> map = new HashMap<>();

        try {
            for (FraudDetectionRule rule : rules) {
                List<CreditRecord> records = rule.validateDateSetAndGetAnomalies(dataSet);
                map.put(rule, records);
            }
        } catch(Exception e) {
            for(Observer observer: onFailureObservers) {
                observer.notify();
            }

            return null;
        }

        for (Observer observer: onSuccessObservers) {
            observer.notify();
        }

        return new CreditFraudResult(map);
    }

    @Override
    public void registerOnSuccessObserver(Observer observer) {
        onSuccessObservers.add(observer);
    }

    @Override
    public void registerOnFailureObserver(Observer observer) {
        onFailureObservers.add(observer);
    }
}
