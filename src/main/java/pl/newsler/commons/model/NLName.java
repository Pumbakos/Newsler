package pl.newsler.commons.model;

import java.io.Serializable;

public interface NLName extends Serializable {
    String getValue();

    boolean validate();
}
