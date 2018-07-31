package de.fforw.skat;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestCase
{
    private final static Logger log = LoggerFactory.getLogger(TestCase.class);

    @Test
    public void logHashedPW()
    {
        logHash("test");
    }

    private void logHash(String password)
    {
        log.debug("Hash for '{}' is {}", password, new BCryptPasswordEncoder().encode(password));
    }
}
