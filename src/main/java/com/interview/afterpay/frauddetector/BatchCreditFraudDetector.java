package com.interview.afterpay.frauddetector;

import com.interview.afterpay.frauddetector.rules.FraudDetectionRule;
import com.interview.afterpay.entities.CreditRecord;
import com.interview.afterpay.entities.CreditFraudResult;
import com.interview.afterpay.entities.FraudResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;


/**
 * Fraud detector that processes each record in a batch file and
 * emits fraudulent records mapped to the ruleSet that have failed against.
 *
 * If the
 */
public class BatchCreditFraudDetector implements DetectorSpec<CreditRecord> {

    private final List<FraudDetectionRule<CreditRecord>> rules = new ArrayList<>();

    private final List<Observer> onSuccessObservers = new ArrayList<>();
    private final List<Observer> onFailureObservers = new ArrayList<>();

    private static final Logger logger = LogManager.getLogger(
        BatchCreditFraudDetector.class.getCanonicalName());

    @Override
    public void addRule(FraudDetectionRule<CreditRecord> rule) {
        this.rules.add(rule);
    }

    @Override
    public FraudResult<CreditRecord> detectAndGetFraudulentRecords(List<CreditRecord> dataSet) {
        logger.info("Starting fraudulent record service for " + dataSet.size() + " records");
        FraudResult<CreditRecord> result = new CreditFraudResult();

        try {
            for (FraudDetectionRule<CreditRecord> rule : rules) {
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
