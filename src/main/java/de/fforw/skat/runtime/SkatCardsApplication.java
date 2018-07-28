package de.fforw.skat.runtime;

import de.fforw.skat.runtime.config.DomainConfiguration;
import de.fforw.skat.runtime.config.GraphQLConfiguration;
import de.fforw.skat.runtime.config.SecurityConfiguration;
import de.fforw.skat.runtime.config.WebConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication(
    exclude = {
        DataSourceAutoConfiguration.class
    },
    scanBasePackages = {
        "de.fforw.skat.runtime.controller",
        "de.fforw.skat.runtime.service"
    }
)
@Import({
    GraphQLConfiguration.class,
    DomainConfiguration.class,
    WebConfiguration.class,
    SecurityConfiguration.class
})


@EnableWebSecurity(debug = true)

@PropertySource({"classpath:skat-${spring.profiles.active}.properties"})
public class SkatCardsApplication
    extends SpringBootServletInitializer
{
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
    {
        return application.sources(SkatCardsApplication.class);
    }


    public static void main(String[] args)
    {
        SpringApplication.run(SkatCardsApplication.class, args);
    }
}
