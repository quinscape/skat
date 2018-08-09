package de.quinscape.domainql.skat.runtime;

import de.quinscape.domainql.skat.runtime.config.DomainConfiguration;
import de.quinscape.domainql.skat.runtime.config.GraphQLConfiguration;
import de.quinscape.domainql.skat.runtime.config.SecurityConfiguration;
import de.quinscape.domainql.skat.runtime.config.WebConfiguration;
import de.quinscape.domainql.skat.runtime.config.WebsocketConfiguration;
import de.quinscape.domainql.skat.runtime.controller.JsEntryPointController;
import de.quinscape.domainql.skat.runtime.game.ShufflingStrategy;
import de.quinscape.domainql.skat.runtime.game.SplitShuffle;
import de.quinscape.domainql.skat.runtime.service.CoreGameLogic;
import de.quinscape.domainql.skat.runtime.service.DefaultShufflingService;
import de.quinscape.domainql.skat.runtime.service.ChannelRepository;
import de.quinscape.domainql.skat.runtime.service.InMemoryChannelRepository;
import de.quinscape.domainql.skat.runtime.game.JavaCollectionsShuffle;
import de.quinscape.domainql.skat.runtime.game.OverhandShuffle;
import de.quinscape.domainql.skat.runtime.game.RiffleShuffle;
import de.quinscape.domainql.skat.runtime.service.ShufflingService;
import de.quinscape.domainql.skat.ws.SkatWebSocketHandler;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.security.SecureRandom;
import java.util.Random;

@SpringBootApplication(
    exclude = {
        DataSourceAutoConfiguration.class
    },
    scanBasePackageClasses = {
        JsEntryPointController.class,
        CoreGameLogic.class
    }
)

@Import({
    GraphQLConfiguration.class,
    WebsocketConfiguration.class,
    DomainConfiguration.class,
    WebConfiguration.class,
    SecurityConfiguration.class
})

@EnableWebSecurity(debug = false)

@PropertySource({"classpath:skat-${spring.profiles.active}.properties"})
public class SkatCardsApplication
    extends SpringBootServletInitializer
    implements ApplicationContextAware
{
    private ApplicationContext applicationContext;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
    {
        return application.sources(SkatCardsApplication.class);
    }

    @Bean
    public ChannelRepository gameRepository(
        SkatWebSocketHandler skatWebSocketHandler
    )
    {
        final InMemoryChannelRepository repository = new InMemoryChannelRepository();

        skatWebSocketHandler.register(repository);

        return repository;
    }

    private static final String NATURAL_SHUFFLE = "naturalShuffle";
    
    @Bean
    public ShufflingService shufflingService()
    {
        return new DefaultShufflingService(
            applicationContext.getBeansOfType(ShufflingStrategy.class),
            NATURAL_SHUFFLE
        );
    }

    @Bean(NATURAL_SHUFFLE)
    public ShufflingStrategy naturalShuffle(Random random)
    {
        return CompositeShufflingStrategy.builder()
            .repeat(3, 4, new OverhandShuffle())
            .split()
            .repeat(4 ,5, new RiffleShuffle())
            .repeat(0 ,1, SplitShuffle.DEFAULT)
            .build();
    }

    @Bean
    public ShufflingStrategy thoroughShuffle(Random random)
    {
        return CompositeShufflingStrategy.builder()
            .repeat(3,4, new OverhandShuffle())
            .repeat(2,3, new RiffleShuffle())
            .split()
            .repeat(3,4, new OverhandShuffle())
            .repeat(2,3, new RiffleShuffle())
            .repeat(0,1, SplitShuffle.DEFAULT)
            .build();
    }

    @Bean
    public ShufflingStrategy javaCollectionsShuffle()
    {
        return new JavaCollectionsShuffle();
    }

    @Bean
    public Random random()
    {
        return new SecureRandom();
    }

    public static void main(String[] args)
    {
        SpringApplication.run(SkatCardsApplication.class, args);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }
}
