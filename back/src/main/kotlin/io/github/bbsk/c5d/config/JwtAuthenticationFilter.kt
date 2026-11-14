package io.github.bbsk.c5d.config

import io.github.bbsk.c5d.service.AuthService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = extractTokenFromCookie(request)

        if (token != null && SecurityContextHolder.getContext().authentication == null) {
            val username = jwtService.extractUsername(token)
            if (username != null && jwtService.isTokenValid(token, username)) {
                val claims = jwtService.extractAllClaims(token)
                val role = claims?.get("role")?.toString() ?: "DISPATCHER"
                val userId = claims?.get("userId")?.toString()
                if (userId != null) {
                    request.setAttribute("userId", userId)
                }
                val authorities = listOf(SimpleGrantedAuthority("ROLE_$role"))

                val authToken = UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    authorities
                )
                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authToken
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun extractTokenFromCookie(request: HttpServletRequest): String? {
        val cookies = request.cookies ?: return null
        return cookies.firstOrNull { it.name == AuthService.ACCESS_TOKEN_COOKIE_NAME }?.value
    }
}
