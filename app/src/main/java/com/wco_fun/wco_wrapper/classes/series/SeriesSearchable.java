package com.wco_fun.wco_wrapper.classes.series;

import org.jsoup.nodes.Element;

public class SeriesSearchable extends Series{
    public SeriesSearchable(String src, String title) {
        super(src, title);
    }

    public SeriesSearchable(Element node) {
        if (node != null) {
            title = node.hasAttr("title")
                    ? node.attr("title")
                    : null;
            src = node.hasAttr("href")
                    ? node.attr("href")
                    : null;
        }
    }
    //return true if the series has a title and a source
    //TODO add a regex to verify the format of the src is a site link
    public boolean isValid() { return !(title.isEmpty() || src.isEmpty()); }
    public boolean contains(String s) { return title.toLowerCase().contains(s.toLowerCase()); }

}
