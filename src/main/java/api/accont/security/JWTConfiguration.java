package api.accont.security;

import api.accont.services.DetailsUserServicesIMPL;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
public class JWTConfiguration extends WebSecurityConfigurerAdapter {

    private final DetailsUserServicesIMPL userService;
    private final PasswordEncoder passwordEncoder;

    public JWTConfiguration(DetailsUserServicesIMPL userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    /*
    Metodo sobre-escrito que informa ao Spring Security para utilizar o serviço de usuario e password Econder para validar a senha
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
    }

    /*
    Metodo sobre-escrito de configuraçao de como o spring security deverá entender a pagina
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // configuração que resolve ataques a aplicação {desabilitado}
        http.csrf().disable().authorizeRequests().antMatchers(HttpMethod.POST, "/login").
                permitAll().anyRequest().authenticated().and().
                addFilter(new JWTAuthenticateFilter(authenticationManager())).
                addFilter(new JWTValidationFilter(authenticationManager())).
                sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    /*
    Metodo que permite que a aplicação receba requisições de outros dominios diferentes dos dela
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource (){
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;

    }
}
