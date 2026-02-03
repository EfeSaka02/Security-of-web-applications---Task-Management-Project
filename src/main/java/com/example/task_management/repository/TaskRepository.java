package com.example.task_management.repository;

import com.example.task_management.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUserId(Long userId);

    Optional<Task> findByIdAndUserId(Long id, Long userId);

    List<Task> findByUserIdAndStatus(Long userId, String status);

    List<Task> findByUserIdAndPriority(Long userId, String priority);
}