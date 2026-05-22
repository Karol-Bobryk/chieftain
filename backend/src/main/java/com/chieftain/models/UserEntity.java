package com.chieftain.models;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class UserEntity {
    @Id
    private String pk_user_id;
}
