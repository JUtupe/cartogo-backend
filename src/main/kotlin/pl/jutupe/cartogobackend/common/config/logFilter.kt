package pl.jutupe.cartogobackend.common.config
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.CommonsRequestLoggingFilter

@Configuration
class RequestLoggingConfig {

    @Bean
    fun logFilter() =
        CommonsRequestLoggingFilter().apply {
            setIncludeQueryString(true)
            setIncludePayload(true)

            setIncludeHeaders(false)

            setMaxPayloadLength(10000)
            setAfterMessagePrefix("Request data: ")
        }
}