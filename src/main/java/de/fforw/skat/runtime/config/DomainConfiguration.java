package de.fforw.skat.runtime.config;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.sql.DataSource;
import javax.transaction.SystemException;
import java.util.Properties;

@Configuration
public class DomainConfiguration
{
    private final static Logger log = LoggerFactory.getLogger(DomainConfiguration.class);

    @Bean
    public AtomikosDataSourceBean atomikosDataSourceBean(

        // all parameters from the @PropertySource defined in DomainqlstartApplication
        @Value("${skat.driver}")
        String driverClassName,
        @Value("${skat.database}")
        String databaseName,
        @Value("${skat.user}")
        String user,
        @Value("${skat.password}")
        String password,
        @Value("${skat.host}")
        String serverName,
        @Value("${skat.port}")
        String portNumber
    )
    {
        final AtomikosDataSourceBean bean = new AtomikosDataSourceBean();

        if (log.isDebugEnabled())
        {
            log.debug("driverClassName", driverClassName);
            log.debug("user", user);
            log.debug("host", serverName);
            log.debug("port", portNumber);
        }

        bean.setXaDataSourceClassName(driverClassName);


        final Properties xaProperties = new Properties();
        xaProperties.setProperty("databaseName", databaseName);
        xaProperties.setProperty("user", user);
        xaProperties.setProperty("password", password);
        xaProperties.setProperty("serverName", serverName);
        xaProperties.setProperty("portNumber", portNumber);
        bean.setXaProperties(xaProperties);

        bean.setMinPoolSize(5);
        bean.setMaxPoolSize(10);

        return bean;
    }

    @Bean
    public DSLContext dslContext(DataSourceConnectionProvider connectionProvider)
    {
        DefaultDSLContext defaultDSLContext = new DefaultDSLContext(
            new DefaultConfiguration()
                .derive(connectionProvider)
                .derive(SQLDialect.POSTGRES)
        );

        log.info("Created DSLContext", defaultDSLContext);

        return defaultDSLContext;
    }

    @Bean
    public DataSourceConnectionProvider connectionProvider(
        DataSource dataSource
    )
    {
        return new DataSourceConnectionProvider(dataSource);
    }

    @Bean(
        initMethod = "init",
        destroyMethod = "close"
    )
    public UserTransactionManager userTransactionManager()
    {
        final UserTransactionManager manager = new UserTransactionManager();
        manager.setForceShutdown(false);
        return manager;
    }

    @Bean
    public UserTransactionImp userTransactionImp() throws SystemException
    {
        final UserTransactionImp transactionImp = new UserTransactionImp();
        transactionImp.setTransactionTimeout(300);
        return transactionImp;
    }

    @Bean
    public JtaTransactionManager jtaTransactionManager() throws SystemException
    {
        final JtaTransactionManager jtaTransactionManager = new JtaTransactionManager();
        jtaTransactionManager.setTransactionManager(userTransactionManager());
        jtaTransactionManager.setUserTransaction(userTransactionImp());
        jtaTransactionManager.setAllowCustomIsolationLevels(true);
        return jtaTransactionManager;
    }

}
