package com.forcelain.awesomebrowser.tabs;

import org.junit.Test;

import static org.junit.Assert.*;

public class UUIDGeneratorTest {

    @Test
    public void testGenerateId() throws Exception {
        UUIDGenerator uuidGenerator = new UUIDGenerator();
        String id = uuidGenerator.generateId();
        assertNotNull(id);
    }
}