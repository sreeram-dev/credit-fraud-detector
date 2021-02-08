package com.interview.afterpay.cfd;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import com.interview.afterpay.entities.CreditFraudResult;
import com.interview.afterpay.frauddetector.BatchCreditFraudDetector;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;
import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;

import com.interview.afterpay.frauddetector.builders.CreditFraudDetectorBuilder;
import com.interview.afterpay.frauddetector.rules.CannotExceedCreditWithdrawal;
import com.interview.afterpay.frauddetector.rules.FraudDetectionRule;
import com.interview.afterpay.processor.CreditFileProcessor;
import com.interview.afterpay.entities.CreditRecord;
import com.interview.afterpay.entities.FraudResult;


@Command(name = "cfd",
    mixinStandardHelpOptions = true,
    version = "cfd 0.1",
    description = "Checks a csv file with credit card statements for fraud")
public class CreditFraudDetectorCli implements Callable<FraudResult<CreditRecord>> {

    static final Logger logger = LogManager.getLogger(CreditFraudDetectorCli.class.getCanonicalName());

    final CreditFileProcessor processor = new CreditFileProcessor();
    final Duration duration = Duration.ofHours(24);

    @Parameters(index = "0", converter = AmountConverter.class,
        description = "Amount in dollars (1.00) to set " +
            "as threshold for fraudulent transactions", paramLabel = "Amount in Dollars (1.00)")
    private Integer amount;

    private File report;

    @Parameters(index = "1",
        description = "Path to the credit report", paramLabel="Credit Report CSV file")
    private void setReport(File file) {
        // If the file does not exist or cannot be read, Throw an exception when parsing the arguments
        if (!file.exists()) {
            throw new ParameterException(spec.commandLine(), "File given does not exist");
        }

        if (file.exists() && !file.canRead()) {
            throw new ParameterException(spec.commandLine(), "File given cannot be read");
        }

        report = file;
    }
    // injected by picocli
    @Spec CommandSpec spec;

    /**
     * Used code from the internet Apache v2 License
     * https://github.com/remkop/picocli/wiki/Configuring-Logging-in-a-CLI-App
     */
    @Option(names = {"-v", "--verbose"},
        description = {
            "Specify multiple -v options to increase verbosity.",
            "For example, `-v -v -v` or `-vvv`"})
    boolean[] verbosity = new boolean[0];

    private void configureLoggers() {
        if (verbosity.length >= 3) {
            Configurator.setRootLevel(Level.TRACE);
        } else if (verbosity.length == 2) {
            Configurator.setRootLevel(Level.DEBUG);
        } else if (verbosity.length == 1) {
            Configurator.setRootLevel(Level.INFO);
        } else {
            Configurator.setRootLevel(Level.WARN);
        }
    }

    public static void main(String... args) {

        CommandLine cmd = new CommandLine(new CreditFraudDetectorCli())
            .setExecutionStrategy(parseResult -> {
                CreditFraudDetectorCli cli =  parseResult.commandSpec().commandLine().getCommand();
                cli.configureLoggers();
                return new CommandLine.RunLast().execute(parseResult);
            });

        int exitCode = cmd.execute(args);
        System.exit(exitCode);
    }

    @Override
    public FraudResult<CreditRecord> call() throws Exception {
        // keeping it separate to make changes according to the file
        List<CreditRecord> records = processor.processFile(report);
        logger.info("Found " + records.size() + " records in the file: " + report.getCanonicalPath());
        // initialise the rules
        List<FraudDetectionRule<CreditRecord>> rules = new ArrayList<>();
        rules.add(new CannotExceedCreditWithdrawal(amount, duration));

        // Detect fraud
        BatchCreditFraudDetector detector = new CreditFraudDetectorBuilder()
            .registerRuleSet(rules)
            .build();

        // print the result
        CreditFraudResult result =  (CreditFraudResult) detector.detectAndGetFraudulentRecords(records);
        Set<String> ids = result.getDistinctHashedIds();
        for (String id: ids) {
            System.out.println(id);
        }

        return result;
    }
}


class AmountConverter implements ITypeConverter<Integer> {

    public Integer convert(String value) throws Exception {
        if  (value.isEmpty()) {
            throw new IllegalArgumentException("Amount is not specified");
        }
        // trim the dollar sign
        if (value.charAt(0) == '$') {
            value = value.substring(1);
        } else if (value.charAt(value.length()-1) == '$') {
            value = value.substring(0, value.length()-1);
        }

        Integer val = (int) (Double.parseDouble(value) * 100);

        return val;
    }
}
