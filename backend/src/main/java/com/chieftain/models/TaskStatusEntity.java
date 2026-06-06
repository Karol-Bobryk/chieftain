package com.chieftain.models;

import com.chieftain.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "task_status_dictionary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatusEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "pk_task_status_id", nullable = false)
  private Integer taskStatusId;

  @Enumerated(EnumType.STRING)
  @Column(name = "status_name", unique = true, nullable = false)
  private TaskStatus statusName;
}
