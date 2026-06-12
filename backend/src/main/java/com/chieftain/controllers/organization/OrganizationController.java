package com.chieftain.controllers.organization;

import com.chieftain.adapters.CustomUserDetails;
import com.chieftain.controllers.organization.dto.OrganizationDetailsResponseDTO;
import com.chieftain.controllers.organization.dto.OrganizationUserResponseDTO;
import com.chieftain.models.OrganizationEntity;
import com.chieftain.models.UserEntity;
import com.chieftain.services.OrganizationService;
import com.chieftain.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {
    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping("/{organizationId}")
    public ResponseEntity<OrganizationDetailsResponseDTO> getOrganizationInfo(
            @PathVariable UUID organizationId,
            @AuthenticationPrincipal CustomUserDetails userDetails){

        if(!userDetails.getOrganization().getPkOrganizationId().equals(organizationId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is in different organization");
        }

        OrganizationEntity organization = organizationService.getOrganizationById(organizationId);
        return ResponseEntity.ok(new OrganizationDetailsResponseDTO(organization.getPkOrganizationId(),
                organization.getName(), organization.getJoinToken(), organization.getCreatedAt()));

    }

    @GetMapping("/{organizationId}/users")
    @Transactional
    public ResponseEntity<PagedModel<OrganizationUserResponseDTO>> getOrganizationUsers(
            @PathVariable UUID organizationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if(!userDetails.getOrganization().getPkOrganizationId().equals(organizationId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is in different organization");
        }

        OrganizationEntity organization = organizationService.getOrganizationById(organizationId);

        Pageable pageable = PageRequest.of(page, size);
        Page<OrganizationUserResponseDTO> usersPage = organizationService.getUsersInOrganization(organization, pageable)
                .map(OrganizationUserResponseDTO::fromUserEntity);

        return ResponseEntity.ok(new PagedModel<>(usersPage));

    }


}
