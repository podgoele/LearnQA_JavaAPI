import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ShortTextTest {

    @ParameterizedTest
    @ValueSource(strings = { "The Chemical Brothers", "Aphex Twin", "Tricky" })
    public void ShortTextTest(String text) {

        assertTrue(text.length() > 15, "Длина текста, который проверяем, больше 15ти символов");

    }
}
