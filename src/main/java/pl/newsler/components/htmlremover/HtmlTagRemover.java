package pl.newsler.components.htmlremover;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.htmlparser.jericho.Renderer;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HtmlTagRemover {
    public static String remove(String html) {
        if (StringUtils.isBlank(html)) {
            return "";
        }

        final Source htmlSource = new Source(html);
        final Segment segment = new Segment(htmlSource, 0, htmlSource.length());
        return new Renderer(segment)
                .setIncludeHyperlinkURLs(true)
                .setConvertNonBreakingSpaces(true)
                .toString();
    }
}
