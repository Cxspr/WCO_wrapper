package com.wco_fun.wco_wrapper.classes;

import org.jsoup.nodes.Element;

public class SeriesSearchable_LE {

    private String title = null;
    private String src = null;
//    private String imgLink = null;

    // public constructor
    public SeriesSearchable_LE(Element node) {
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

    public boolean subStringMatches(String comp) {
        if (comp.length() < title.length()) {
            String subString = title.substring(0, comp.length());
            return subString.equalsIgnoreCase(comp);
        } else {
            return false;
        }
    }

    public boolean contains(String comp) {
        return title.toLowerCase().contains(comp.toLowerCase());
    }

    // src getter
    public String getSrc() {
        return src;
    }

}

