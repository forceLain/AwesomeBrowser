package com.forcelain.awesomebrowser.tabs;

import java.util.UUID;

public class UUIDGenerator implements TabIdGenerator {
    @Override
    public String generateId() {
        return UUID.randomUUID().toString();
    }
}
