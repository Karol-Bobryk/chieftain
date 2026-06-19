package com.chieftain.controllers.group.dto;

import com.chieftain.controllers.user.dto.UserDisplayDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetMembersResponseDTO {
  List<UserDisplayDTO> members;
}
