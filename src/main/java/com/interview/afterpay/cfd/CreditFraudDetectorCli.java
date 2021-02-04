package com.interview.afterpay.cfd;

import com.interview.afterpay.cfd.frauddetector.DetectorSpec;
import com.interview.afterpay.cfd.frauddetector.builders.CreditFraudDetectorBuilder;
import com.interview.afterpay.cfd.frauddetector.rules.CannotExceedCreditWithdrawal;
import com.interview.afterpay.cfd.frauddetector.rules.FraudDetectionRule;
import com.interview.afterpay.cfd.processor.CreditFileProcessor;
import com.interview.afterpay.cfd.record.CreditRecord;
import com.interview.afterpay.cfd.result.FraudResult;
import picocli.CommandLine;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;


@CommandLine.Command(name = "cfd",
    mixinStandardHelpOptions = true,
    version = "cfd 0.1",
    description = "Checks a csv file with credit card statements for fraud")
public class CreditFraudDetectorCli implements Callable<Integer> {

    CreditFileProcessor processor = new CreditFileProcessor();
    Duration duration = Duration.ofHours(24);

    @CommandLine.Parameters(index = "0", converter = AmountConverter.class,
        description = "Amount in dollars and cents (10.00) to set " +
            "as threshold for fraudulent transactions")
    private Integer amount;

    @CommandLine.Parameters(index = "1",
        description = "Path to the credit report")
    private File report;

    public static void main(String... args) {
        int exitCode = new CommandLine(new CreditFraudDetectorCli()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        // keeping it separate to make changes according to the file
        List<CreditRecord> records = processor.processFile(report);

        // initialise the rules
        List<FraudDetectionRule> rules = new ArrayList<>();
        rules.add(new CannotExceedCreditWithdrawal(amount, duration));

        // Detect fraud
        DetectorSpec detector =  new CreditFraudDetectorBuilder()
            .registerRuleSet(rules)
            .build();

        // print the result
        FraudResult result = detector.detectAndGetFraudulentRecords(records);

        result.serializeResult();

        return 0;
    }
}


class AmountConverter implements CommandLine.ITypeConverter<Integer> {

    public Integer convert(String value) throws Exception {
        String[] arr = value.split(".");
        if (arr[1].length() != 2) {
            throw new IllegalArgumentException("Only 2 values denoting cents after the dot");
        }


        for(char ch: value.toCharArray()) {
            if (ch == '.') {
                continue;
            }
            if (!Character.isDigit(ch)) {
                throw new IllegalArgumentException("Only digits allowed");
            }
        }

        Integer val = (int) (Double.parseDouble(value) * 100);
        return val;
    }
}
