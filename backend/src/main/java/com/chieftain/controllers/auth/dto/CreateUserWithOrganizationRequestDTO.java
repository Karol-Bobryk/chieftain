package com.chieftain.controllers.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserWithOrganizationRequestDTO {

  @NotBlank(message = "Name cannot be blank")
  @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
  private String name;

  @NotBlank(message = "Surname cannot be blank")
  @Size(min = 2, max = 255, message = "Surname must be between 2 and 255 characters")
  private String surname;

  @NotBlank(message = "Email address cannot be blank")
  @Email(message = "Email address is invalid")
  private String emailAddress;

  @NotBlank(message = "Password cannot be blank")
  @Size(min = 2, max = 255, message = "Password must be between 2 and 255 characters")
  private String password;

  @NotBlank(message = "Job title cannot be blank")
  @Size(min = 2, max = 255, message = "Job title must be between 2 and 255 characters")
  private String jobTitle;

  @NotBlank(message = "Organization name cannot be blank")
  @Size(min = 2, max = 255, message = "Organization name must be between 2 and 255 characters")
  String organizationName;
}
