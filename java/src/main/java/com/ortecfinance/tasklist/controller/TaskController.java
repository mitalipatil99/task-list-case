package com.ortecfinance.tasklist.controller;

import com.ortecfinance.tasklist.model.Task;
import com.ortecfinance.tasklist.service.ShareInstance;
import com.ortecfinance.tasklist.service.TaskService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/projects")
public class TaskController {

    private final TaskService taskService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public TaskController() {
        this.taskService = ShareInstance.getInstance();
    }

//    @GetMapping
//    public List<String> getTasks() {
//        return Arrays.asList("Task 1", "Task 2", "Task 3");
//   }
    @PostMapping
    public ResponseEntity<String> createProject(@RequestBody String name) {  //create a project
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Project name is required.");
        }
        taskService.addProject(name);
        return ResponseEntity.ok("Project created: " + name);
    }
    // example : curl -X POST http://localhost:8080/projects \
    //  -H "Content-Type: application/json" \
    //  -d 'MyProject'


    @GetMapping
    public ResponseEntity<Map<String, List<Task>>> getTasks() {  //get all tasks
        return ResponseEntity.ok(taskService.getAllTasks());
    }
    //  curl -X GET http://localhost:8080/projects

    @PostMapping("/{project}/tasks") // create a task under a project
    public ResponseEntity<?> addTask(
            @PathVariable String project,
            @RequestBody String taskDescription) {
        if (taskDescription == null || taskDescription.isEmpty()) {
            return ResponseEntity.badRequest().body("Task description is required.");
        }
        try {
            Task task = taskService.addTask(project, taskDescription);
            return ResponseEntity.ok(task);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Project not found.");
        }
    }
    // curl -X POST http://localhost:8080/projects/MyProject/tasks \
    //  -H "Content-Type: application/json" \
    //  -d 'test'


    @PutMapping("/{project}/tasks/{taskId}")  //update deadline of a task
    public ResponseEntity<String> updateDeadline(
            @PathVariable String project,
            @PathVariable long taskId,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate deadline) {
        try {
            taskService.setDeadline(taskId, deadline);
            return ResponseEntity.ok("Deadline set for task " + taskId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Task not found.");
        }
    }
    //curl -X PUT "http://localhost:8080/projects/MyProject/tasks/1?deadline=31-12-2025"


    @GetMapping("/view_by_deadline") //get tasks grouped by deadline
    public ResponseEntity<Map<String, Map<String, List<Task>>>> getViewByDeadline() {
        Map<String, Map<String, List<Task>>> result = new LinkedHashMap<>();
        Map<LocalDate, Map<String, List<Task>>> groupedByDate = taskService.getTasksGroupedByDeadline();
        for (Map.Entry<LocalDate, Map<String, List<Task>>> entry : groupedByDate.entrySet()) {
            String dateKey = (entry.getKey() != null)
                    ? entry.getKey().format(formatter)
                    : "No deadline";
            result.put(dateKey, entry.getValue());
        }
        return ResponseEntity.ok(result);
    }
    // curl -X GET http://localhost:8080/projects/view_by_deadline
}
