package com.examples;

import org.testng.annotations.Test;

public class TestClass2 {

    @Test(groups = {Groups.UNIT_TESTS})
    public void test3() {
        System.out.println("Test 3");
    }

    @Test(groups = {Groups.API_TESTS})
    public void test4() {
        System.out.println("Test 4");
    }
}
