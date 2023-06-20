/*
 * MyLibraryTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */

package com.github.toolarium.network.proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;


/**
 * MyLibraryTest.
 *
 * <p>! This is just a sample please remove it. !</p>
 */
public class MyLibraryTest {
    /**
     * Test MyLibrary method.
     */
    @Test void testSomeLibraryMethod() {
        MyLibrary classUnderTest = new MyLibrary();
        assertTrue(classUnderTest.someLibraryMethod(), "someLibraryMethod should return 'true'");
    }
    
    
    /**
     * Test Version.
     */
    @Test void testVersion() {
        assertEquals(Version.VERSION, Version.getVersion());
        new Version();
        Version.main(new String[]{});
    }
}
