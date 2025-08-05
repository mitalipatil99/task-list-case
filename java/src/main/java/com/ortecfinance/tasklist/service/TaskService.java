package com.ortecfinance.tasklist.service;
import com.ortecfinance.tasklist.model.Task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TaskService {

    private final Map<String, List<Task>> tasksByProject = new LinkedHashMap<>();
    private long lastId = 0;

    public void addProject(String name) {
        tasksByProject.putIfAbsent(name, new ArrayList<Task>());
    }

    public Task addTask(String projectName, String description) {
        List<Task> projectTasks = tasksByProject.get(projectName);
        if (projectTasks == null) throw new IllegalArgumentException("Project not found");
        Task task = new Task(nextId(), description);
        projectTasks.add(task);
        return task;
    }

    public void setDeadline(long taskId, LocalDate deadline) {
        Task task = findTask(taskId);
        task.setDeadline(deadline);
    }

    public void setDone(long taskId, boolean done) {
        Task task = findTask(taskId);
        task.setDone(done);
    }

    public Map<String, List<Task>> getAllTasks() {
        return tasksByProject;
    }

    public List<Task> getTasksDueToday() {
        LocalDate today = LocalDate.now();
        List<Task> todayTasks = new ArrayList<>();
        for (List<Task> tasks : tasksByProject.values()) {
            for (Task task : tasks) {
                if (task.getDeadline() != null && task.getDeadline().equals(today)) {
                    todayTasks.add(task);
                }
            }
        }
        return todayTasks;
    }

    public Map<LocalDate, Map<String, List<Task>>> getTasksGroupedByDeadline() {
        Map<LocalDate, Map<String, List<Task>>> grouped = new TreeMap<>(Comparator.nullsLast(Comparator.naturalOrder()));
        for (Map.Entry<String, List<Task>> entry : tasksByProject.entrySet()) {
            for (Task task : entry.getValue()) {
                Map<String, List<Task>> byDate = grouped.computeIfAbsent(task.getDeadline(), k -> new LinkedHashMap<>());
                List<Task> taskList = byDate.computeIfAbsent(entry.getKey(), k -> new ArrayList<>());
                taskList.add(task);
            }
        }
        return grouped;
    }

    private Task findTask(long id) {
        for (List<Task> tasks : tasksByProject.values()) {
            for (Task task : tasks) {
                if (task.getId() == id) return task;
            }
        }
        throw new NoSuchElementException("Task with ID " + id + " not found.");
    }

    private long nextId() {
        return ++lastId;
    }
}
