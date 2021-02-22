package com.examples;

import org.testng.annotations.Test;

public class TestClass3 {

    public static final String GLOBAL_NAME = "global_name";

    @Test(groups = {Groups.UNIT_TESTS})
    public void test5() {
        System.out.println("Test 5");
    }

    @Test(groups = {Groups.UNIT_TESTS})
    public void test6() {
        System.out.println("Test 6");
    }
}
