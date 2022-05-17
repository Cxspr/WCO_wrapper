package com.example.wco_wrapper.classes;

import org.jsoup.nodes.Element;

public class Episode {
    private String title = null;
    private String src = null;

    // public constructor
    public Episode(Element node) {
        if (node != null) {
            title = node.hasAttr("title")
                    ? node.attr("title")
                    : null;
            src = node.hasAttr("href")
                    ? node.attr("href")
                    : null;
        }
    }

    // return true if series has a title and src
    public boolean isValid() {
        return !(title.isEmpty() || src.isEmpty());
    }

    // title getter
    public String getTitle() {
        return title;
    }
    // src getter
    public String getSrc() {
        return src;
    }

}
