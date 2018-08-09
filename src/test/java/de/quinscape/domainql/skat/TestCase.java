package de.quinscape.domainql.skat;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestCase
{
    private final static Logger log = LoggerFactory.getLogger(TestCase.class);

    @Ignore
    @Test
    public void logHashedPW()
    {
        logHash("SYSTEM");
    }

    private void logHash(String password)
    {
        log.debug("Hash for '{}' is {}", password, new BCryptPasswordEncoder().encode(password));
    }
}


