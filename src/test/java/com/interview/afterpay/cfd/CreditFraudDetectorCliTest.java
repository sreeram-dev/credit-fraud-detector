package com.interview.afterpay.cfd;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.interview.afterpay.entities.CreditFraudResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import picocli.CommandLine;
import picocli.CommandLine.ParameterException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class CreditFraudDetectorCliTest {
    final PrintStream originalOut = System.out;
    final PrintStream originalErr = System.err;
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final ByteArrayOutputStream err = new ByteArrayOutputStream();


    @BeforeEach
    public void setUpStreams() {
        out.reset();
        err.reset();
        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(err));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void testMissingParameters() {
        String[] args = {"src/test/resources/testcase_invalid.csv"};
        CommandLine cmd = new CommandLine(new CreditFraudDetectorCli());
        assertThrows(ParameterException.class, () -> cmd.parseArgs(args));

        int exitCode = cmd.execute(args);
        // CMD itself has thrown an error, so the exitcode is 2
        assertEquals(2, exitCode);
    }
    ;
    @Test
    public void testBadAmountParameter() {
        String[] args = {"abc", "src/test/resources/testcase_invalid.csv"};
        CommandLine cmd = new CommandLine(new CreditFraudDetectorCli());
        assertThrows(ParameterException.class, () -> cmd.parseArgs(args));
    }

    @Test
    public void testFileNotFound() {
        String[] args = {"150.00", "asdasdsad"};
        CommandLine cmd = new CommandLine(new CreditFraudDetectorCli());
        assertThrows(ParameterException.class, () -> cmd.parseArgs(args));
    }

    @Test
    public void testHelpOption() {
        String[] args = {"-h"};
        CommandLine cmd = new CommandLine(new CreditFraudDetectorCli());
        int exitCode = cmd.execute(args);
        assertEquals(0, exitCode);
        assertThat(out.toString(), notNullValue());
        assertThat(out.toString(), is(not(emptyString())));
    }

    @Test
    public void testCLISuccess() {
        String[] args = {"150.00", "src/test/resources/testcase_valid_1.csv"};
        CommandLine cmd = new CommandLine(new CreditFraudDetectorCli());
        cmd.execute(args);
        CreditFraudResult result = cmd.getExecutionResult();
        List<String> records = new ArrayList<>(result.getDistinctHashedIds());

        assertEquals(1, records.size());
        assertThat(records, hasItem("10d7ce2f43e35fa57d1bbf8b1e2"));
        String[] actualOutput = out.toString().split("\n");
        assertArrayEquals(new String[]{"10d7ce2f43e35fa57d1bbf8b1e2"}, actualOutput);
    }

    @Test
    public void testCLIEmptyOutput() {
        String[] args = {"1500.00", "src/test/resources/testcase_valid_1.csv"};
        CommandLine cmd = new CommandLine(new CreditFraudDetectorCli());
        cmd.execute(args);
        CreditFraudResult result = cmd.getExecutionResult();
        List<String> records = new ArrayList<>(result.getDistinctHashedIds());

        // There should be no records because they contain the records size.
        assertEquals(0, records.size());
        String[] actualOutput = out.toString().split("\n");
        assertArrayEquals(new String[]{""}, actualOutput);
    }

    /**
     * Tests for the report where the initial window is larger than 24H
     */
    @Test
    public void testCLILargerThan24HValid() {
        String[] args = {"150.00", "src/test/resources/testcase_valid_2.csv"};
        CommandLine cmd = new CommandLine(new CreditFraudDetectorCli());
        int exitCode = cmd.execute(args);
        CreditFraudResult result = cmd.getExecutionResult();
        List<String> records = new ArrayList<>(result.getDistinctHashedIds());

        // There should be no records because they contain the records size.
        assertEquals(0, exitCode);
        assertEquals(1, records.size());
        String[] actualOutput = out.toString().split("\n");
        // hamcrest notation is <actual, expected_matcher>
        assertThat(actualOutput, hasItemInArray("10d7ce2f43e35fa57d1bbf8b1e2"));
        assertThat(actualOutput, not(hasItemInArray("00f14573aa584c5094b2d9acdf8")));
    }

    @Test
    public void testCLIOverlappingFrauds() {
        String[] args = {"150.00", "src/test/resources/testcase_valid_overlapping.csv"};
        CommandLine cmd = new CommandLine(new CreditFraudDetectorCli());
        int exitCode =  cmd.execute(args);
        CreditFraudResult result = cmd.getExecutionResult();
        List<String> records = new ArrayList<>(result.getDistinctHashedIds());

        // There should be no records because they contain the records size.
        assertEquals(0, exitCode);
        assertEquals(2, records.size());
        String[] actualOutput = out.toString().split("\n");
        assertThat(new ArrayList<>(Arrays.asList(actualOutput)),
            hasItems("10d7ce2f43e35fa57d1bbf8b1e2", "20e6ce2f43e35fa57d1bbf8b1e2"));

        assertThat(actualOutput, not(hasItemInArray("00f14573aa584c5094b2d9acdf8")));
    }

    @Test
    public void testInvalidCSVFile() {
        String[] args = {"150.00", "src/test/resources/testcase_invalid.csv"};
        CommandLine cmd = new CommandLine(new CreditFraudDetectorCli());

        int exitCode = cmd.execute(args);

        // Command returned a general exception which was handled by the cli
        // https://www.gnu.org/software/bash/manual/html_node/Exit-Status.html
        //https://picocli.info/#_exception_exit_codes
        assertEquals(1, exitCode);
    }
}
