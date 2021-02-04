package com.interview.afterpay.cfd.frauddetectors;

import com.interview.afterpay.cfd.frauddetectors.rules.FraudDetectionRule;
import com.interview.afterpay.cfd.record.CreditRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observer;


public class BatchCreditFraudDetector implements DetectorSpec<CreditRecord> {

    private List<FraudDetectionRule> rules = new ArrayList<>();

    private List<Observer> onSuccessObservers = new ArrayList<>();
    private List<Observer> onFailureObservers = new ArrayList<>();

    private List<CreditRecord> fraudulentRecords = new ArrayList<>();

    @Override
    public void addRule(FraudDetectionRule rule) {
        rules.add(rule);
    }

    @Override
    public HashMap<FraudDetectionRule, List<CreditRecord>>
        detectAndGetFraudulentRecords(List<CreditRecord> dataSet) {

        HashMap<FraudDetectionRule, List<CreditRecord>> map = new HashMap<>();
        for (FraudDetectionRule rule: rules) {
            List<CreditRecord> records = rule.validateDateSetAndGetAnomalies(dataSet);
            map.put(rule, records);
        }

        return map;
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
