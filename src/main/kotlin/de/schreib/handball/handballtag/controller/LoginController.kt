package de.schreib.handball.handballtag.controller

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin()
@RequestMapping("/login")
class LoginController {
    @GetMapping
    fun getAuthenticatedUser(@AuthenticationPrincipal user:UserDetails): UserDetails {
        return user
    }
}