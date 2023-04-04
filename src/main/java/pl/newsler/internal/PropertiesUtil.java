package pl.newsler.internal;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PropertiesUtil {
    public static boolean arePropsSet(String prop, String... props) {
        if (StringUtils.isBlank(prop)) {
            return false;
        }
        if (props == null) {
            return false;
        } else {
            for (final String s : props) {
                if (StringUtils.isBlank(s)) {
                    return false;
                }
            }
        }

        return true;
    }
}
