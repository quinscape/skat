package de.fforw.skat;

import de.fforw.skat.runtime.SkatCardsApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {
	SkatCardsApplication.class
})
public class SkatCardsApplicationTests
{

	@Test
	public void contextLoads() {
	}

}
