package com.chieftain.controllers.group.dto;

import com.chieftain.models.UserEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublicGroupMemberDTO {
    String name;
    String surname;

    public static PublicGroupMemberDTO ofEntity(UserEntity user) {
        return new PublicGroupMemberDTO(
                user.getName(),
                user.getSurname()
        );
    }
}
