package pl.newsler.internal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PropertiesUtilTest {
    @Test
    void shouldReturnTrueWhenPropertyAreSetCorrectly() {
        Assertions.assertTrue(PropertiesUtil.arePropsSet("prop1"));
    }

    @Test
    void shouldReturnTrueWhenPropertiesAreSetCorrectly() {
        Assertions.assertTrue(PropertiesUtil.arePropsSet("prop1", "prop2", "prop3", "prop4"));
    }

    @Test
    void shouldReturnFalseWhenPropertyAreSetIncorrectly() {
        Assertions.assertFalse(PropertiesUtil.arePropsSet(""));
        Assertions.assertFalse(PropertiesUtil.arePropsSet("   "));
        Assertions.assertFalse(PropertiesUtil.arePropsSet(null));
    }

    @Test
    void shouldReturnFalseWhenPropertiesAreSetIncorrectly() {
        Assertions.assertFalse(PropertiesUtil.arePropsSet("prop1", "", "prop3", "prop4"));
        Assertions.assertFalse(PropertiesUtil.arePropsSet("prop1", "", null, "prop4"));
        Assertions.assertFalse(PropertiesUtil.arePropsSet(null, "", null, "prop4"));
        Assertions.assertFalse(PropertiesUtil.arePropsSet(null, "prop2", null, "prop4"));
        Assertions.assertFalse(PropertiesUtil.arePropsSet(null, "prop2", "prop3", "prop4"));
        Assertions.assertFalse(PropertiesUtil.arePropsSet("", "prop2", "prop3", "prop4"));
        Assertions.assertFalse(PropertiesUtil.arePropsSet("prop1", null));
        Assertions.assertFalse(PropertiesUtil.arePropsSet("", null));
        Assertions.assertFalse(PropertiesUtil.arePropsSet(null, null));
        Assertions.assertFalse(PropertiesUtil.arePropsSet(null, new String[]{}));
    }
}