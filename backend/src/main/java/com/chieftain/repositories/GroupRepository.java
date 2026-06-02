package com.chieftain.repositories;

import com.chieftain.models.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GroupRepository extends JpaRepository<GroupEntity, UUID>{
}
