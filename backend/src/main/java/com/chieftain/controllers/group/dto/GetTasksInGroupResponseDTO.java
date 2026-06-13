package com.chieftain.controllers.group.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetTasksInGroupResponseDTO {
  List<RootTaskDisplayDTO> tasks;
}
