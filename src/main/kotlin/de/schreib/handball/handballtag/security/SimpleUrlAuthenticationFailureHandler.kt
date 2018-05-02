package de.schreib.handball.handballtag.security

import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class SimpleUrlAuthenticationFailureHandler : AuthenticationFailureHandler {
    override fun onAuthenticationFailure(request: HttpServletRequest?, response: HttpServletResponse?, exception: AuthenticationException?) {
        val message = "Authentication failed ${exception?.message}"
        response?.sendError(HttpServletResponse.SC_UNAUTHORIZED, message)
    }
}