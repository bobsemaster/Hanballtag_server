package de.schreib.handball.handballtag.security

import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler

const val BASIC_USER = "BASIC_USER"
const val SPIELLEITER = "SPIELLEITER"
const val KAMPFGERICHT = "KAMPFGERICHT"

@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
class SecurityConfig : WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity?) {
        if (http == null) {
            throw NullPointerException("Http was null")
        }
        http.authorizeRequests()
                //Man muss für jeden Request authentifiziert sein
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login").failureHandler(SimpleUrlAuthenticationFailureHandler())
                .successHandler(StatusCodeAuthenticationSuccessHandler())
                .permitAll()
                .and().exceptionHandling().authenticationEntryPoint(Http403ForbiddenEntryPoint())
                .and().logout().permitAll()
                .and().cors().disable()
                .csrf().disable()
    }

    @Bean
    override fun userDetailsService(): UserDetailsService {


        val basicUser: UserDetails = User.withUsername("benutzer")
                // passwort = GeheimesBenutzerPasswortDasKeinerRausfindenWird
                .password("{bcrypt}\$2a\$10\$K/ODeo7.tPPuZK/tUbhV2uVDPZzdzYhR9eLTGnmKosAyyCszcGP9y")
                .roles(BASIC_USER)
                .build()

        val kampfgerichtUser: UserDetails = User.withUsername("kampfgericht")
                // HandballKuchenBallSteakSpass
                .password("{bcrypt}\$2a\$10\$F.ENlyXReCh/Gmn8eu.08OBNkLMqMN7/je36A/ceHz4YUQUTxjg0S")
                .roles(KAMPFGERICHT)
                .build()
        val spielleiterUser: UserDetails = User.withUsername("spielleiter")
                // AdminForstenriedArbeitWeniger
                .password("{bcrypt}\$2a\$10\$s2DfUXc4fe9Jr7vPVSeoEOnXv.etJyaszrk0msejWFoxEG5XiSNr2")
                .roles(SPIELLEITER)
                .build()
        return InMemoryUserDetailsManager(basicUser, kampfgerichtUser, spielleiterUser)
    }
}


