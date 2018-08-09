package de.quinscape.domainql.skat.runtime.config;

import de.quinscape.domainql.skat.runtime.controller.GraphQLController;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

@Configuration
@Import(MethodSecurityConfiguration.class)
// Enable method security ( with @PreAuthorize/@PostAuthorize annotations)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration
    extends WebSecurityConfigurerAdapter
{

    private final DSLContext dslContext;

    private final boolean allowDevGraphQLAccess;


    private final static String[] PUBLIC_URIS = new String[]
        {
            "/index.jsp",
            "/error",
            "/js/**",
            GraphQLController.GRAPHQL_URI,
            GraphQLController.GRAPHQL_DEV_URI,
            "/css/**",
            "/webfonts/**"
        };


    @Autowired
    public SecurityConfiguration(
        DSLContext dslContext,
        @Value("${skat.graphql.dev:false}")
        boolean allowDevGraphQLAccess
    )
    {
        this.dslContext = dslContext;
        this.allowDevGraphQLAccess = allowDevGraphQLAccess;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http
            .authorizeRequests()
            .antMatchers(
                PUBLIC_URIS
            ).permitAll()

            .antMatchers("/admin/**")
                .hasRole("ADMIN")

            .antMatchers("/game/**")
                .hasRole("USER")

            .and()
                .formLogin()
                    .loginPage("/login")
                    .loginProcessingUrl("/login_check")
                    .defaultSuccessUrl("/game/")
                    .permitAll()
            .and()

            // exempt GRAPHQL_DEV_URI from CSRF requirements if allowDevGraphQLAccess is set
            .csrf()
                .requireCsrfProtectionMatcher(
                    new AllowDevGraphQLAccess(allowDevGraphQLAccess)
                )
            .and()
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .deleteCookies("remember-me")
                .and()
                .rememberMe()
                    .key("tv1-Ser8O=x;#kjNthN8")
                    .tokenRepository(persistentTokenRepository())
                    .userDetailsService(userDetailsServiceBean());

    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception
    {
        auth
            .userDetailsService(userDetailsServiceBean())
            .passwordEncoder(new BCryptPasswordEncoder());

    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository()
    {
        return new DefaultPersistentTokenRepository(dslContext);
    }


    @Bean
    @Override
    public UserDetailsService userDetailsServiceBean()
    {
        return new AppAuthenticationService(
            dslContext
        );
    }
}
