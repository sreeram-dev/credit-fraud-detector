package com.interview.afterpay.cfd;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;

import com.interview.afterpay.entities.CreditFraudResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
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
    public void testCLISuccess() {
        String[] args = {"150.00", "src/test/resources/testcase_valid_1.csv"};
        CommandLine cmd = new CommandLine(new CreditFraudDetectorCli());
        cmd.execute(args);
        CreditFraudResult result = cmd.getExecutionResult();
        List<String> records = new ArrayList<>(result.getDistinctHashedIds());

        assertEquals(1, records.size());
        assertEquals("10d7ce2f43e35fa57d1bbf8b1e2", records.get(0));
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
        cmd.execute(args);
        CreditFraudResult result = cmd.getExecutionResult();
        List<String> records = new ArrayList<>(result.getDistinctHashedIds());

        // There should be no records because they contain the records size.
        assertEquals(1, records.size());
        String[] actualOutput = out.toString().split("\n");
        assertThat(actualOutput, hasItemInArray("10d7ce2f43e35fa57d1bbf8b1e2"));
        assertThat(actualOutput, not(hasItemInArray("00f14573aa584c5094b2d9acdf8")));
    }
}
