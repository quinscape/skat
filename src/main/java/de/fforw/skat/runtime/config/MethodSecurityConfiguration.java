package de.fforw.skat.runtime.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;


@Configuration
public class MethodSecurityConfiguration
    extends GlobalMethodSecurityConfiguration
{
    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler()
    {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(new OwnerPermissionEvaluator());
        return expressionHandler;
    }
}
