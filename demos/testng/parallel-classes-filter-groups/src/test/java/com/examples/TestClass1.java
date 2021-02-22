package com.examples;

import org.testng.annotations.Test;

public class TestClass1 {

    @Test(groups = {Groups.API_TESTS})
    public void test1() {
        System.out.println("Test 1");
    }

    @Test(groups = {Groups.UI_TESTS})
    public void test2() {
        System.out.println("Test 2");
    }
}
