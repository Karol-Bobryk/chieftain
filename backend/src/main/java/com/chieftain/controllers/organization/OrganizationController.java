package com.chieftain.controllers.organization;

import com.chieftain.adapters.CustomUserDetails;
import com.chieftain.controllers.organization.dto.OrganizationDetailsResponseDTO;
import com.chieftain.controllers.organization.dto.OrganizationUserResponseDTO;
import com.chieftain.models.OrganizationEntity;
import com.chieftain.services.OrganizationService;
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



}
