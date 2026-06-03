package com.chieftain.models;

import com.chieftain.enums.GroupUserPermission;
import com.chieftain.enums.SystemRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "group_user_permission_dictionary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupUserPermissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_permission_id", nullable = false)
    private Integer permissionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "permission_name", unique = true, nullable = false)
    private GroupUserPermission permissionName;
}
