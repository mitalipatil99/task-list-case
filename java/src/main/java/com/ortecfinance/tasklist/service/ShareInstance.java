package com.ortecfinance.tasklist.service;

public class ShareInstance {
    private static final TaskService INSTANCE = new TaskService();
    public static TaskService getInstance() {
        return INSTANCE;
    }
}