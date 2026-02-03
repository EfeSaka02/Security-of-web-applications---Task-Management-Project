package com.example.task_management.controller;

import com.example.task_management.dto.CreateTaskRequest;
import com.example.task_management.dto.UpdateTaskRequest;
import com.example.task_management.model.Task;
import com.example.task_management.service.TaskService;
import com.example.task_management.model.Task;
import com.example.task_management.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping // It brings the all tasks
    public ResponseEntity<List<Task>> getAllTasks() { // It only returns tasks for authenticated users.
        List<Task> task = taskService.getAllTasksForCurrentUser();
        return ResponseEntity.ok(task);
    }

    @GetMapping("/{id}") // Thats one brings the specific task
    public ResponseEntity<?> getTaskById(@PathVariable Long id) { // Access control: Only the task owner can see it.
        try {
            Task task = taskService.getTaskById(id);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            logger.warn("Unauthorized access attempt to task: {} - Error: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @PostMapping // Creating new task
    public ResponseEntity<?> createTask(@Valid @RequestBody CreateTaskRequest request) { // It connects the authenticated user automatically
        try {
            Task task = taskService.createTask(request);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}") // Deleting the task
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id); // Only task's owner can able to delete it
            logger.info("Task deleted successfully: {}", id);
            return ResponseEntity.ok("Task deleted successfully");
        } catch (RuntimeException e) {
            logger.warn("Unauthorized delete attempt for task: {} - Error: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/status/{status}") // Filter of the status
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable String status) {
        List<Task> tasks = taskService.getTasksByStatus(status); // Filters only from their own tasks.
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/priority/{priority}") // Priority filter
    public ResponseEntity<List<Task>> getTasksByPriority(@PathVariable String priority) {
        List<Task> tasks = taskService.getTasksByPriority(priority);
        return ResponseEntity.ok(tasks);
    }
}


