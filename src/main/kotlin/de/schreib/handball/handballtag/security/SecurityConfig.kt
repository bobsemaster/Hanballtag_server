package de.schreib.handball.handballtag.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
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
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import javax.sql.DataSource

const val BASIC_USER = "BASIC_USER"
const val SPIELLEITER = "SPIELLEITER"
const val KAMPFGERICHT = "KAMPFGERICHT"

@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
class SecurityConfig : WebSecurityConfigurerAdapter() {
    @Autowired
    lateinit var source: DataSource

    override fun configure(http: HttpSecurity?) {
        if (http == null) {
            throw NullPointerException("Http was null")
        }
        http.authorizeRequests().antMatchers("/ws/**").permitAll()
            //Man muss für jeden Request authentifiziert sein
            .anyRequest().authenticated().and().formLogin().loginPage("/login")
            .failureHandler(SimpleUrlAuthenticationFailureHandler())
            .successHandler(StatusCodeAuthenticationSuccessHandler()).permitAll().and().rememberMe()
            .tokenRepository(persistentTokenRepository()).key("AppKey").alwaysRemember(true)
            .rememberMeParameter("rememberMe").rememberMeCookieName("User")
            // 1 Monat eingeloggt bleiben
            .tokenValiditySeconds(30 * 24 * 60 * 60).and().exceptionHandling()
            .authenticationEntryPoint(Http403ForbiddenEntryPoint()).and().logout().permitAll().and().cors().and().csrf()
            .disable()
    }

    // https://stackoverflow.com/questions/40418441/spring-security-cors-filter
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val corsConfiguration = CorsConfiguration()
        corsConfiguration.allowedOrigins = listOf("*")
        corsConfiguration.allowedMethods = listOf("HEAD", "GET", "POST", "PUT", "DELETE", "PATH")
        // setAllowCredentials(true) is important, otherwise:
        // The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'.
        corsConfiguration.allowCredentials = true
        corsConfiguration.allowedHeaders = listOf("Authorization", "Cache-Control", "Content-Type")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfiguration)
        return source
    }

    //Braucht table der nicht automatisch erzeugt wird
    // create table persistent_logins (username varchar(64) not null, series varchar(64) primary key,token varchar(64) not null, last_used timestamp not null)
    @Bean
    fun persistentTokenRepository(): PersistentTokenRepository {
        val jdbcTokenRepositoryImpl = JdbcTokenRepositoryImpl()
        jdbcTokenRepositoryImpl.setDataSource(source)
        return jdbcTokenRepositoryImpl
    }


    @Bean
    override fun userDetailsService(): UserDetailsService {


        val basicUser: UserDetails = User.withUsername("benutzer")
            // passwort = GeheimesBenutzerPasswortDasKeinerRausfindenWird
            .password("{bcrypt}\$2a\$10\$K/ODeo7.tPPuZK/tUbhV2uVDPZzdzYhR9eLTGnmKosAyyCszcGP9y").roles(BASIC_USER)
            .build()

        val kampfgerichtUser: UserDetails = User.withUsername("kampfgericht")
            // HandballKuchenBallSteakSpass
            .password("{bcrypt}\$2a\$10\$F.ENlyXReCh/Gmn8eu.08OBNkLMqMN7/je36A/ceHz4YUQUTxjg0S").roles(KAMPFGERICHT)
            .build()
        val spielleiterUser: UserDetails = User.withUsername("spielleiter")
            // AdminForstenriedArbeitWeniger
            .password("{bcrypt}\$2a\$10\$s2DfUXc4fe9Jr7vPVSeoEOnXv.etJyaszrk0msejWFoxEG5XiSNr2")
            .roles(SPIELLEITER, KAMPFGERICHT).build()
        return InMemoryUserDetailsManager(basicUser, kampfgerichtUser, spielleiterUser)
    }


}


