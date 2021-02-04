package com.interview.afterpay.cfd;

import picocli.CommandLine;

import java.util.concurrent.Callable;


@CommandLine.Command(name = "cfd",
    mixinStandardHelpOptions = true,
    version = "cfd 0.1",
    description = "Checks a csv file with credit card statements for fraud")
public class CreditFraudDetectorCli implements Callable<Integer> {

    public static void main(String... args) {
        int exitCode = new CommandLine(new CreditFraudDetectorCli()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        return 0;
    }
}
