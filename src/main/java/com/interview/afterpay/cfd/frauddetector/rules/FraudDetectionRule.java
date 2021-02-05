package com.interview.afterpay.cfd.frauddetector.rules;

import java.util.List;

public interface FraudDetectionRule<T> {
    List<T> validateDateSetAndGetAnomalies(List<T> dataSet);

    String getRuleName();
}
