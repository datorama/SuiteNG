package com.examples;

import org.testng.annotations.Test;

import java.util.logging.Logger;

public class TestClass1 {

    private final static Logger LOGGER = Logger.getLogger(TestClass3.class.getName());

    @Test(groups = {Groups.API_TESTS})
    public void test1() {
        LOGGER.info("Test 1");
    }

    @Test(groups = {Groups.UI_TESTS})
    public void test2() {
        LOGGER.info("Test 2");
    }
}
