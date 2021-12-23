package arguments;

import io.github.divios.jcommands.arguments.types.IntegerArgument;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class typesTest {

    @Test
    public void testValidArgument() {
        Assert.assertTrue(new IntegerArgument().isValidArgument("3"));
    }

    @Test
    public void testInvalidArgument() {
        Assert.assertFalse(new IntegerArgument().isValidArgument("aaaa"));
    }

    @Test
    public void testCompetition() {
        IntegerArgument argument = new IntegerArgument();
        argument.overrideSuggestions(() -> Arrays.asList(3, 4, 5));

        List<String> expected = Arrays.asList("3", "4", "5");

        for (int i = 0; i < expected.size(); i++) {
            Assert.assertEquals(expected.get(i), argument.getSuggestions().get(i));
        }
    }

}
