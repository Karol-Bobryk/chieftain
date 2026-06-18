package com.chieftain.controllers.group;


import com.chieftain.controllers.group.dto.PublicGroupMemberDTO;
import com.chieftain.controllers.group.dto.PublicGroupResponseDTO;
import com.chieftain.controllers.group.dto.PublicGroupTaskDTO;
import com.chieftain.models.GroupEntity;
import com.chieftain.repositories.GroupRepository;
import com.chieftain.repositories.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@RestController
@RequestMapping("/api/public")
public class PublicGroupController {


    private final GroupRepository groupRepository;
    private final TaskRepository taskRepository;

    public PublicGroupController(GroupRepository groupRepository, TaskRepository taskRepository) {
        this.groupRepository = groupRepository;
        this.taskRepository = taskRepository;
    }

    @GetMapping("/groups/{shareToken}/tasks")
    @Transactional
    public ResponseEntity<List<PublicGroupTaskDTO>> getPublicGroupTasks(
            @PathVariable String shareToken){
        GroupEntity group = groupRepository.findByShareToken(shareToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
        List<PublicGroupTaskDTO> tasks = taskRepository.findByGroupId(group.getId())
                .stream().map(task -> PublicGroupTaskDTO.ofEntity(task)).toList();

        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/groups/{shareToken}/members")
    @Transactional
    public ResponseEntity<List<PublicGroupMemberDTO>> getPublicGroupMembers(
            @PathVariable String shareToken){
        GroupEntity group = groupRepository.findByShareToken(shareToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
        List<PublicGroupMemberDTO> members = group.getMembers()
                .stream().map(member -> PublicGroupMemberDTO.ofEntity(member)).toList();

        return ResponseEntity.ok(members);

    }

    @GetMapping("/groups/{shareToken}")
    @Transactional
    public ResponseEntity<PublicGroupResponseDTO> getPublicGroupInfo(
            @PathVariable String shareToken){
        GroupEntity group = groupRepository.findByShareToken(shareToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));

        List<PublicGroupMemberDTO> members = group.getMembers()
                .stream().map(member -> PublicGroupMemberDTO.ofEntity(member)).toList();

        List<PublicGroupTaskDTO> tasks = taskRepository.findByGroupId(group.getId())
                .stream().map(task -> PublicGroupTaskDTO.ofEntity(task)).toList();

        return ResponseEntity.ok(new PublicGroupResponseDTO(group.getName(), members, tasks));

    }

}
