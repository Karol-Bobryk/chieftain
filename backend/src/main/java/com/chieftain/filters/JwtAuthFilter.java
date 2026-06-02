package com.chieftain.filters;

import com.chieftain.adapters.CustomUserDetails;
import com.chieftain.services.CustomUserDetailsService;
import com.chieftain.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

  private final CustomUserDetailsService customUserDetailsService;

  public JwtAuthFilter(CustomUserDetailsService customUserDetailsService) {
    this.customUserDetailsService = customUserDetailsService;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    // TODO: add some constant like excluded paths, change in security config as well
    return request.getServletPath().startsWith("/auth");
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    String header = request.getHeader("Authorization");
    if (header == null || !header.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    // to exclude the bearer part
    String token = header.substring(7);
    try {
      UUID userId = JwtService.getUserId(token);

      if (SecurityContextHolder.getContext().getAuthentication() == null) {
        CustomUserDetails customUserDetails = customUserDetailsService.loadUserById(userId);
        if (JwtService.isTokenValidForUser(token, customUserDetails)) {
          UsernamePasswordAuthenticationToken authenticationToken =
              new UsernamePasswordAuthenticationToken(
                  customUserDetails, null, customUserDetails.getAuthorities());

          authenticationToken.setDetails(
              new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
      }

    } catch (Exception exception) {
      // TODO: add better errors
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write("Invalid JWT");
      return;
    }

    filterChain.doFilter(request, response);
  }
}
