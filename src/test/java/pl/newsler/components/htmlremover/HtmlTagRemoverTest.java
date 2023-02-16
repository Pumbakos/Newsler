package pl.newsler.components.htmlremover;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class HtmlTagRemoverTest {
    @Test
    @DisplayName("should remove <h1>, <h2> and <p> html tags")
    void shouldRemoveSimpleHTMLTags() {
        final String html = "<h1>Test TinyMCE</h1> <h2>Test TinyMCE</h2> <p><em>Test TinyMCE</em></p> <p style=\"text-align: right;\">Test TinyMCE</p>";
        String removed = HtmlTagRemover.remove(html);
        Assertions.assertNotNull(removed);
        Assertions.assertFalse(removed.contains("<h1>"));
        Assertions.assertFalse(removed.contains("<h2>"));
        Assertions.assertFalse(removed.contains("<p>"));
    }
    @Test
    @DisplayName("should remove <p>, <em>, <strong>, <span>, <s>, <sup>, <sub> and <code> html tags & styling")
    void shouldRemoveCodeHTMLTags() {
        final String html = """
                <p>Normal</p>
                <p><em>Italic</em></p>
                <p><strong>Bold</strong></p>
                <p><span style="text-decoration: underline;">Underline</span></p>
                <p><s><span style="text-decoration: underline;">Strikethrough</span></s></p>
                <p>X<sup>Superscript</sup></p>
                <p>X<sub>Subscript</sub></p>
                <p><code>Code</code></p>
                """;
        String removed = HtmlTagRemover.remove(html);
        Assertions.assertNotNull(removed);
        Assertions.assertFalse(removed.contains("<p>"));
        Assertions.assertFalse(removed.contains("<em>"));
        Assertions.assertFalse(removed.contains("<strong>"));
        Assertions.assertFalse(removed.contains("<span>"));
        Assertions.assertFalse(removed.contains("<s>"));
        Assertions.assertFalse(removed.contains("<sup>"));
        Assertions.assertFalse(removed.contains("<sub>"));
        Assertions.assertFalse(removed.contains("<code>"));
        Assertions.assertFalse(removed.contains("style"));
        Assertions.assertFalse(removed.contains("text-decoration: underline>"));
    }

    @Test
    @DisplayName("should remove <table>, <colgroup>, <col>, <tr>, <td> and <tbody> html tags")
    void shouldRemoveTableHTMLTags() {
        final String html = """
                <table style="border-collapse: collapse; width: 100%;" border="1">
                  <colgroup>
                    <col style="width: 31.8408%;">
                    <col style="width: 31.8408%;">
                    <col style="width: 31.8408%;">
                    <col style="width: 2.23881%;">
                    <col style="width: 2.23881%;">
                  </colgroup>
                  <tbody>
                  <tr>
                    <td>Cell 1</td>
                    <td>Cell 2</td>
                    <td>Cell 3</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                  </tr>
                  <tr>
                    <td>Cell 4</td>
                    <td>Cell 5</td>
                    <td>Cell 6</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                  </tr>
                  </tbody>
                </table>
                """;
        String removed = HtmlTagRemover.remove(html);
        Assertions.assertNotNull(removed);
        Assertions.assertFalse(removed.contains("<table>"));
        Assertions.assertFalse(removed.contains("<colgroup>"));
        Assertions.assertFalse(removed.contains("<col>"));
        Assertions.assertFalse(removed.contains("<tr>"));
        Assertions.assertFalse(removed.contains("<td>"));
        Assertions.assertFalse(removed.contains("<tbody>"));
    }

    @Test
    @DisplayName("should remove any custom tag")
    void shouldRemoveCustomHtmlTag() {
        Assertions.assertEquals("Tag content", HtmlTagRemover.remove("<nl-custom-tag>Tag content</nl-custom-tag>"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "\n", "\t", "\r"})
    @NullSource
    @DisplayName("should return empty String when data null or empty or blank")
    void shouldRemoveNullAndEmptyString(String html) {
        Assertions.assertEquals("", HtmlTagRemover.remove(html));
    }

    @Test
    @DisplayName("should not remove any tags when just String")
    void shouldNotRemoveAnyTagWhenJustString() {
        Assertions.assertEquals("Just a string", HtmlTagRemover.remove("Just a string"));
    }

    @Test
    @DisplayName("should not remove any tags when tag empty")
    void shouldNotRemoveAnyTagWhenTagEmpty() {
        Assertions.assertEquals("<></>", HtmlTagRemover.remove("<></>"));
    }
}