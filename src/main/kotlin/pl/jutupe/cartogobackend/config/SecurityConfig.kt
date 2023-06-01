package pl.jutupe.cartogobackend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import pl.jutupe.cartogobackend.auth.application.JwtTokenFilter
import pl.jutupe.cartogobackend.auth.domain.AuthService
import javax.servlet.http.HttpServletResponse


@Configuration
class SecurityConfig(
    private val authService: AuthService,
    private val jwtTokenFilter: JwtTokenFilter,
) {

    @Bean
    fun userDetailsService(): UserDetailsService {
        return UserDetailsService { googleToken -> authService.principalByUserToken(googleToken) }
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/v1/auth/google").permitAll()
            .anyRequest().authenticated()
            .and()
            .exceptionHandling()
            .authenticationEntryPoint { _, response, ex ->
                ex.printStackTrace()
                response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    ex.message
                )
            }
            .and()
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
}