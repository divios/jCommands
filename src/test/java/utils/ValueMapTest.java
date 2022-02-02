package utils;

import io.github.divios.jcommands.util.Value;
import io.github.divios.jcommands.util.ValueMap;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

public class ValueMapTest {

    @Test
    public void createTest() {
        Map<String, Value> values = new LinkedHashMap<>();

        values.put("target", Value.ofString("DiviosX"));
        values.put("shop", Value.ofString("drops"));

        ValueMap valueMap = ValueMap.of(values);
    }

    @Test
    public void testOrder() {
        Map<String, Value> values = new LinkedHashMap<>();

        values.put("target", Value.ofString("DiviosX"));
        values.put("shop", Value.ofString("drops"));

        ValueMap valueMap = ValueMap.of(values);
        Assert.assertEquals(valueMap.get(0).getAsString(), "DiviosX");
        Assert.assertEquals(valueMap.get(1).getAsString(), "drops");
        Assert.assertEquals(valueMap.get("target").getAsString(), "DiviosX");
        Assert.assertEquals(valueMap.get("shop").getAsString(), "drops");
    }

    @Test
    public void testOrderWithCommand() {
        Map<String, Value> values = new LinkedHashMap<>();

        values.put("target", Value.ofString("DiviosX"));
        values.put("shop", Value.ofString("drops"));

        ValueMap valueMap = ValueMap.of(values);
        Assert.assertEquals(valueMap.get(0).getAsString(), "DiviosX");
        Assert.assertEquals(valueMap.get(1).getAsString(), "drops");
        Assert.assertEquals(valueMap.get("target").getAsString(), "DiviosX");
        Assert.assertEquals(valueMap.get("shop").getAsString(), "drops");
    }

}
