package com.interview.afterpay.cfd;

import java.io.File;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.logging.log4j.core.config.Configurator;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;
import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.IExecutionExceptionHandler;
import picocli.CommandLine.IParameterExceptionHandler;
import picocli.CommandLine.ParameterException;

import com.interview.afterpay.cfd.frauddetector.DetectorSpec;
import com.interview.afterpay.cfd.frauddetector.builders.CreditFraudDetectorBuilder;
import com.interview.afterpay.cfd.frauddetector.rules.CannotExceedCreditWithdrawal;
import com.interview.afterpay.cfd.frauddetector.rules.FraudDetectionRule;
import com.interview.afterpay.cfd.processor.CreditFileProcessor;
import com.interview.afterpay.cfd.entities.CreditRecord;
import com.interview.afterpay.cfd.entities.FraudResult;


@Command(name = "cfd",
    mixinStandardHelpOptions = true,
    version = "cfd 0.1",
    description = "Checks a csv file with credit card statements for fraud")
public class CreditFraudDetectorCli implements Callable<String> {

    static final Logger logger = LogManager.getLogger(CreditFraudDetectorCli.class.getCanonicalName());

    CreditFileProcessor processor = new CreditFileProcessor();
    Duration duration = Duration.ofHours(24);

    @Parameters(index = "0", converter = AmountConverter.class,
        description = "Amount in dollars and cents (10.00) to set " +
            "as threshold for fraudulent transactions")
    private Integer amount;

    @Parameters(index = "1",
        description = "Path to the credit report")
    private File report;

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
            })
            .setParameterExceptionHandler(new StackTracePrintHandler());
            //.setExecutionExceptionHandler(new BusinessStackTraceHandler());

        int exitCode = cmd.execute(args);
        String result = cmd.getExecutionResult();
        System.out.println(result);
        logger.info("Result: " + result);
        System.exit(exitCode);
    }

    @Override
    public String call() throws Exception {
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

        return "hello";
    }
}


class AmountConverter implements ITypeConverter<Integer> {
    static final Logger logger = LogManager.getLogger(CreditFraudDetectorCli.class.getCanonicalName());


    public Integer convert(String value) throws Exception {
        Integer val = (int) (Double.parseDouble(value) * 100);
        return val;
    }
}

/**
 * Taken from the documentation
 * https://picocli.info/
 */
class StackTracePrintHandler implements IParameterExceptionHandler {

    public int handleParseException(ParameterException ex, String[] args) {
        CommandLine cmd = ex.getCommandLine();
        PrintWriter err = cmd.getErr();

        // if tracing at DEBUG level, show the location of the issue
        if ("DEBUG".equalsIgnoreCase(System.getProperty("picocli.trace"))) {
            err.println(cmd.getColorScheme().stackTraceText(ex));
        }
        ex.printStackTrace(err);

        err.println(cmd.getColorScheme().errorText(ex.getMessage())); // bold red
        CommandLine.UnmatchedArgumentException.printSuggestions(ex, err);
        err.print(cmd.getHelp().fullSynopsis());

        CommandLine.Model.CommandSpec spec = cmd.getCommandSpec();
        err.printf("Try '%s --help' for more information.%n", spec.qualifiedName());

        return cmd.getExitCodeExceptionMapper() != null
            ? cmd.getExitCodeExceptionMapper().getExitCode(ex)
            : spec.exitCodeOnInvalidInput();
    }
}


/**
 * Modified code from documentation
 * https://picocli.info/
 */
class BusinessStackTraceHandler implements IExecutionExceptionHandler {
    public int handleExecutionException(Exception ex,
                                        CommandLine cmd,
                                        CommandLine.ParseResult parseResult) {

        // bold red error message
        cmd.getErr().println(cmd.getColorScheme().errorText(ex.getMessage()));
        PrintWriter err = cmd.getErr();
        ex.printStackTrace(err);

        return cmd.getExitCodeExceptionMapper() != null
            ? cmd.getExitCodeExceptionMapper().getExitCode(ex)
            : cmd.getCommandSpec().exitCodeOnExecutionException();
    }
}
