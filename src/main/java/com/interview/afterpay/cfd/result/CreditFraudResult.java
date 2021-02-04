package com.interview.afterpay.cfd.result;

import com.interview.afterpay.cfd.frauddetector.rules.FraudDetectionRule;
import com.interview.afterpay.cfd.record.CreditRecord;

import java.util.HashMap;
import java.util.List;

public class CreditFraudResult implements FraudResult {

    HashMap<FraudDetectionRule, List<CreditRecord>> rawData;

    public CreditFraudResult(HashMap<FraudDetectionRule, List<CreditRecord>> rawData) {
        this.rawData = rawData;
    }

    @Override
    public String serializeResult() {
        return null;
    }
}
