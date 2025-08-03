package com.ortecfinance.tasklist.service;
import com.ortecfinance.tasklist.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


class TaskServiceTest {

        private TaskService service;

        @BeforeEach
        void setUp() {
            service = new TaskService();
        }
    @Test
    void canAddProjectAndTask() {
        service.addProject("project1");
        Task task = service.addTask("project1", "Do something");

        assertEquals(1, task.getId());
        assertEquals("Do something", task.getDescription());
        assertFalse(task.isDone());

        Map<String, List<Task>> allTasks = service.getAllTasks();
        assertTrue(allTasks.containsKey("project1"));
        assertEquals(1, allTasks.get("project1").size());
    }
    @Test
    void addingTaskToUnknownProjectThrows() {
        Exception e = assertThrows(IllegalArgumentException.class, () ->
                service.addTask("missing", "task"));
        assertEquals("Project not found", e.getMessage());
    }
    @Test
    void canSetDeadline() {
        service.addProject("p");
        Task task = service.addTask("p", "Deadline task");

        LocalDate deadline = LocalDate.of(2025, 1, 1);
        service.setDeadline(task.getId(), deadline);

        assertEquals(deadline, task.getDeadline());
    }
    @Test
    void canCheckAndUncheckTask() {
        service.addProject("proj");
        Task task = service.addTask("proj", "done task");

        service.setDone(task.getId(), true);
        assertTrue(task.isDone());

        service.setDone(task.getId(), false);
        assertFalse(task.isDone());
    }
    @Test
    void getTasksDueTodayOnly() {
        service.addProject("proj");
        Task t1 = service.addTask("proj", "today");
        Task t2 = service.addTask("proj", "tomorrow");

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        service.setDeadline(t1.getId(), today);
        service.setDeadline(t2.getId(), tomorrow);

        List<Task> dueToday = service.getTasksDueToday();
        assertEquals(1, dueToday.size());
        assertEquals(t1.getId(), dueToday.get(0).getId());
    }
    @Test
    void tasksAreGroupedByDeadline() {
        service.addProject("x");
        Task t1 = service.addTask("x", "task 1");
        Task t2 = service.addTask("x", "task 2");
        Task t3 = service.addTask("x", "task 3");

        LocalDate d1 = LocalDate.of(2025, 8, 1);
        LocalDate d2 = LocalDate.of(2025, 8, 2);

        service.setDeadline(t1.getId(), d1);
        service.setDeadline(t2.getId(), d2);
        // no deadline for t3

        Map<LocalDate, Map<String, List<Task>>> grouped = service.getTasksGroupedByDeadline();
        assertEquals(3, grouped.size());

        assertTrue(grouped.containsKey(d1));
        assertTrue(grouped.containsKey(d2));
        assertTrue(grouped.containsKey(null));  // for t3
    }

}
