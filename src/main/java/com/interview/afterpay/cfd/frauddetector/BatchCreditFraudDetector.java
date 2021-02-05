package com.interview.afterpay.cfd.frauddetector;

import com.interview.afterpay.cfd.frauddetector.rules.FraudDetectionRule;
import com.interview.afterpay.cfd.entities.CreditRecord;
import com.interview.afterpay.cfd.entities.CreditFraudResult;
import com.interview.afterpay.cfd.entities.FraudResult;

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

        FraudResult result = new CreditFraudResult();

        try {
            for (FraudDetectionRule rule : rules) {
                List<CreditRecord> records = rule.validateDateSetAndGetAnomalies(dataSet);
                result.addRuleAndRecords(rule, records);
            }
        } catch(Exception e) {
            for(Observer observer: onFailureObservers) {
                observer.notify();
            }
            e.printStackTrace();
            return null;
        }

        for (Observer observer: onSuccessObservers) {
            observer.notify();
        }

        return result;
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
