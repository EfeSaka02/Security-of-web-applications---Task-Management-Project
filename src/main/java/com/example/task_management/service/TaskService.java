package com.example.task_management.service;

import com.example.task_management.dto.CreateTaskRequest;
import com.example.task_management.dto.UpdateTaskRequest;
import com.example.task_management.model.Task;
import com.example.task_management.repository.TaskRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;

    public TaskService(TaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication(); // Takes the authentication from SecurityContext
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        UserDetails userDetails = (UserDetails) auth.getPrincipal(); // Extract the username from UserDetails.
        String username = userDetails.getUsername();

        return userService.findByUsername(username).getId(); // Find user from the user and take the ID
    }

    @Transactional
    public Task createTask(CreateTaskRequest request) {
        Long userId = getCurrentUserId();

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus() != null ? request.getStatus() : "TODO");
        task.setPriority(request.getPriority() != null ? request.getPriority() : "MEDIUM");
        task.setUserId(userId);

        return taskRepository.save(task);
    }

    public List<Task> getAllTasksForCurrentUser() {
        Long userId = getCurrentUserId();
        return taskRepository.findByUserId(userId);
    }

    public Task getTaskById(Long taskId) {
        Long userId = getCurrentUserId();

        return taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new RuntimeException("Task not found or access denied"));
    }

    @Transactional
    public Task updateTask(Long taskId, UpdateTaskRequest request) {
        Long userId = getCurrentUserId();

        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new RuntimeException("Task not found or access denied"));

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }

        return taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        Long userId = getCurrentUserId();

        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new RuntimeException("Task not found or access denied"));

        taskRepository.delete(task);
    }

    public List<Task> getTasksByStatus(String status) {
        Long userId = getCurrentUserId();
        return taskRepository.findByUserIdAndStatus(userId, status);
    }

    public List<Task> getTasksByPriority(String priority) {
        Long userId = getCurrentUserId();
        return taskRepository.findByUserIdAndPriority(userId, priority);
    }
}