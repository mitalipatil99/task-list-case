package com.ortecfinance.tasklist.cli;

import com.ortecfinance.tasklist.model.Task;
import com.ortecfinance.tasklist.service.TaskService;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TaskListConsole implements Runnable {
    private static final String QUIT = "quit";
    private final TaskService service;
    private final BufferedReader in;
    private final PrintWriter out;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public TaskListConsole(BufferedReader reader, PrintWriter writer) {
        this.service = new TaskService();
        this.in = reader;
        this.out = writer;
    }

    public void run() {
        out.println("Welcome to TaskList! Type 'help' for available commands.");
        while (true) {
            out.print("> ");
            out.flush();
            String command;
            try {
                command = in.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (command.equals(QUIT)) {
                break;
            }
            executeCommand(command);
        }
    }

    private void executeCommand(String commandLine) {
        try {
            String[] commandRest = commandLine.split(" ", 2);
            String command = commandRest[0];
            switch (command) {
                case "show":
                    show();
                    break;
                case "add":
                    add(commandRest[1]);
                    break;
                case "deadline":
                    deadline(commandRest[1]);
                    break;
                case "today":
                    today();
                    break;
                case "view-by-deadline":
                    viewByDeadline();
                    break;
                case "check":
                    check(commandRest[1]);
                    break;
                case "uncheck":
                    uncheck(commandRest[1]);
                    break;
                case "help":
                    printHelp();
                    break;
                default:
                    out.printf("I don't know what the command \"%s\" is.", command);
                    out.println();
                    break;
            }
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
        }
    }

    private void add(String args) {
        if (args.startsWith("project ")) {
            service.addProject(args.substring(8));
        } else if (args.startsWith("task ")) {
            String[] split = args.substring(5).split(" ", 2);
            service.addTask(split[0], split[1]);
        }
    }

    private void show() {
        for (Map.Entry<String, List<Task>> project : service.getAllTasks().entrySet()){
            out.println(project.getKey());
            for (Task task : project.getValue()) {
                String deadline = task.getDeadline() != null ? " (Due: " + task.getDeadline().format(formatter) + ")" : "";
                out.printf("    [%c] %d: %s%s%n", task.isDone() ? 'x' : ' ', task.getId(), task.getDescription(), deadline);
            }
        }
    }

    private void today() {
        List<Task> tasks = service.getTasksDueToday();
        if (tasks.isEmpty()) {
            out.println("No tasks are due today.");
        } else {
            for (Task task : tasks) {
                out.printf("[%c] %d: %s (Due: %s)%n", task.isDone() ? 'x' : ' ', task.getId(), task.getDescription(), task.getDeadline().format(formatter));
            }
        }
    }

    private void deadline(String args) {
        String[] split = args.split(" ", 2);
        service.setDeadline(Long.parseLong(split[0]), LocalDate.parse(split[1], formatter));
    }

    private void viewByDeadline() {
        for (Map.Entry<LocalDate,Map<String, List<Task>>> grouped : service.getTasksGroupedByDeadline().entrySet()) {
            String date = (grouped.getKey() != null) ? grouped.getKey().format(formatter) : "No deadline";
            out.println(date + ":");
            for (Map.Entry<String, List<Task>> project : grouped.getValue().entrySet())  {
                out.println("    " + project.getKey() + ":");
                for (Task task : project.getValue()) {
                    out.printf("        %d: %s%n", task.getId(), task.getDescription());
                }
            }
        }
    }
    private void check(String args){
        service.setDone(Long.parseLong(args), true);

    }
    private void uncheck(String args){
        service.setDone(Long.parseLong(args), false);

    }

    private void printHelp() {
        out.println("Commands:");
        out.println("  show");
        out.println("  today");
        out.println("  add project <project name>");
        out.println("  add task <project name> <task description>");
        out.println("  deadline <task ID> <date 'dd-MM-yyyy'>");
        out.println("  check <task ID>");
        out.println("  uncheck <task ID>");
        out.println("  view-by-deadline");
        out.println();
    }
}
