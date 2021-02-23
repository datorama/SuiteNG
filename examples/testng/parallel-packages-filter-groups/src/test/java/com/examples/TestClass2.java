package com.examples;

import org.testng.annotations.Test;

import java.util.logging.Logger;

public class TestClass2 {

    private final static Logger LOGGER = Logger.getLogger(TestClass2.class.getName());

    @Test(groups = {Groups.UNIT_TESTS})
    public void test3() {
        LOGGER.info("Test 3");
    }

    @Test(groups = {Groups.API_TESTS})
    public void test4() {
        LOGGER.info("Test 4");
    }
}
