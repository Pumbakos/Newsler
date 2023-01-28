package pl.newsler;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Due to failing workflows -> issue with dockerized tests")
class NewslerApplicationTests {
    @Test
    void contextLoads() {
    }
}
