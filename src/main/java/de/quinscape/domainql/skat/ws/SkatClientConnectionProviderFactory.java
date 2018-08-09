package de.quinscape.domainql.skat.ws;

import de.quinscape.domainql.param.ParameterProvider;
import de.quinscape.domainql.param.ParameterProviderFactory;
import org.springframework.context.ApplicationContext;

import java.lang.annotation.Annotation;

public final class SkatClientConnectionProviderFactory
    implements ParameterProviderFactory
{
    private final ApplicationContext applicationContext;


    public SkatClientConnectionProviderFactory(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }


    @Override
    public ParameterProvider createIfApplicable(Class<?> parameterClass, Annotation[] annotations) throws Exception
    {
        if (parameterClass.equals(SkatClientConnection.class))
        {
            return new SkatClientConnectionProvider(applicationContext.getBean(SkatWebSocketHandler.class));
        }
        return null;
    }
}
