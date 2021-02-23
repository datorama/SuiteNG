package com.examples;

import org.testng.annotations.Test;
import java.util.logging.Logger;

public class TestClass3 {

    private final static Logger LOGGER = Logger.getLogger(TestClass3.class.getName());
    public final static Integer TIMEOUT = Integer.parseInt(System.getProperty("TIMEOUT"));

    @Test(groups = {Groups.UNIT_TESTS})
    public void test5() {
        LOGGER.info("Test 5 - " + TIMEOUT);
    }

    @Test(groups = {Groups.UNIT_TESTS})
    public void test6() {
        LOGGER.info("Test 6");
    }
}
