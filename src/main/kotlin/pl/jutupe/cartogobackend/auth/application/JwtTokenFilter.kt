package pl.jutupe.cartogobackend.auth.application

import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.util.ObjectUtils
import org.springframework.web.filter.OncePerRequestFilter
import pl.jutupe.cartogobackend.auth.domain.UserPrincipal
import pl.jutupe.cartogobackend.user.infrastructure.UserRepository
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class JwtTokenFilter(
    private val jwtUtil: JwtTokenUtil,
    private val userRepository: UserRepository,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (!hasAuthorizationBearer(request)) {
            filterChain.doFilter(request, response)
            return
        }

        val token = getAccessToken(request)

        if (!jwtUtil.validateAccessToken(token)) {
            filterChain.doFilter(request, response)
            return
        }

        setAuthenticationContext(token, request)
        filterChain.doFilter(request, response)
    }

    private fun hasAuthorizationBearer(request: HttpServletRequest): Boolean {
        val header = request.getHeader("Authorization")

        return !(ObjectUtils.isEmpty(header) || !header.startsWith("Bearer"))
    }

    private fun getAccessToken(request: HttpServletRequest): String {
        val header = request.getHeader("Authorization")

        return header
            .split(" ".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray<String>()[1]
            .trim { it <= ' ' }
    }

    private fun setAuthenticationContext(token: String, request: HttpServletRequest) {
        val userDetails = getUserDetails(token)
        val authentication = UsernamePasswordAuthenticationToken(userDetails, null, null)

        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

        SecurityContextHolder.getContext().authentication = authentication
    }

    private fun getUserDetails(token: String): UserDetails? {
        val userId = jwtUtil.getSubject(token)!!

        val user = userRepository.findByIdOrNull(userId) ?: return null

        return UserPrincipal(user)
    }
}