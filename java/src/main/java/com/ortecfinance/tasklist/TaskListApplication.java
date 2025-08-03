package com.ortecfinance.tasklist;

import com.ortecfinance.tasklist.cli.TaskListConsole;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

@SpringBootApplication
public class TaskListApplication {

    public static void main(String[] args) {
            System.out.println("Starting console Application");
        new Thread(() -> {
            System.out.println("Starting console...");
            TaskListConsole console = new TaskListConsole(
                    new BufferedReader(new InputStreamReader(System.in)),
                    new PrintWriter(System.out, true));
            console.run();
        }).start();
            SpringApplication.run(TaskListApplication.class, args);
            System.out.println("localhost:8080/tasks");
        }
}
