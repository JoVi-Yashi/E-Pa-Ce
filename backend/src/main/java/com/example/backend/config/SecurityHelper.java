package com.example.backend.config;

import com.example.backend.auth.entity.ParticipanteEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityHelper {

    /**
     * Get the currently authenticated user from SecurityContext.
     */
    public ParticipanteEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }
        return (ParticipanteEntity) authentication.getPrincipal();
    }

    /**
     * Check if current user has a specific authority.
     */
    public boolean hasAuthority(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            return false;

        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(authority) || a.getAuthority().equals("ALL:ALL"));
    }

    /**
     * Check if user is the owner of an entity or has 'ALL' management rights.
     */
    public boolean isOwnerOrHasAll(Long ownerId, String allPermission) {
        ParticipanteEntity currentUser = getCurrentUser();
        if (currentUser == null)
            return false;

        // Admin or super-user with ALL:ALL always passes
        if (hasAuthority("ALL:ALL") || hasAuthority(allPermission)) {
            return true;
        }

        // Check ownership if they don't have global permission
        return currentUser.getDocumentoIdentidad().equals(ownerId);
    }
}
