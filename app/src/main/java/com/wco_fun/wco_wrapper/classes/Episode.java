package com.wco_fun.wco_wrapper.classes;

import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Episode {
    private String title = null;
    private String src = null;
    private String abrTitle = null;

    // public constructor
    public Episode(Element node) {
        if (node != null) {
            title = node.hasAttr("title")
                    ? node.attr("title")
                    : null;
            src = node.hasAttr("href")
                    ? node.attr("href")
                    : null;
            abrTitle = abrString();
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

    public String getAbrTitle() { return abrTitle; }
    //will attempt to parse the title for a few variations of title structures to abbreviate the episode index
    //Special Cases
    //Demon Slayer (does not use conventional season notations)
    public String abrString() {
        String res = "";

        //Season xyz Episode xyz
        Pattern pattern = Pattern.compile(".*Season [0-9]* Episode [0-9]*", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(title);
        if (matcher.find()){
            int a = title.lastIndexOf("Season "),b = title.indexOf("Episode ");
            a+=("Season ").length();
            res = "S";
            String sNum = title.substring(a,b-1);
//            sNum.trim(); //might be able to get rid of
            res = res.concat(sNum + " Ep. ");
            b+=("Episode ").length();
            for (b=b ; b < title.length(); b++) {
                try {
                    int eNum = Integer.parseInt(String.valueOf(title.charAt(b)));
                    //if here then char was an int
                    res = res.concat(String.valueOf(title.charAt(b)));
                } catch (NumberFormatException e) {
                    if (title.charAt(b)=='.' || title.charAt(b)=='-') {
                        res = res.concat(String.valueOf(title.charAt(b)));
                        continue;
                    }

                    break;
                }
            }
            return res;
        }

        //OVA Episode xyz
        pattern = Pattern.compile(".*OVA Episode [0-9]*", Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(title);
        if (matcher.find()){
            res = "OVA Ep. ";
            int b = title.indexOf("Episode ") + ("Episode ").length();
            for (b=b ; b < title.length(); b++) {
                try {
                    int eNum = Integer.parseInt(String.valueOf(title.charAt(b)));
                    //if here then char was an int
                    res = res.concat(String.valueOf(title.charAt(b)));
                } catch (NumberFormatException e) {
                    if (title.charAt(b)=='.' || title.charAt(b)=='-') {
                        res = res.concat(String.valueOf(title.charAt(b)));
                        continue;
                    }
                    break;
                }
            }
            return res;
        }

        //Episode xyz
        pattern = Pattern.compile(".*Episode [0-9]*", Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(title);
        if (matcher.find()){
            res = "Ep. ";
            int b = title.indexOf("Episode ") + ("Episode ").length();
            for (b=b ; b < title.length(); b++) {
                try {
                    int eNum = Integer.parseInt(String.valueOf(title.charAt(b)));
                    //if here then char was an int
                    res = res.concat(String.valueOf(title.charAt(b)));
                } catch (NumberFormatException e) {
                    if (title.charAt(b)=='.' || title.charAt(b)=='-') {
                        res = res.concat(String.valueOf(title.charAt(b)));
                        continue;
                    }
                    break;
                }
            }
            return res;
        }

        //Movie xyz: movie title
        pattern = Pattern.compile(".*Movie[ :[0-9]]*", Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(title);
        if (matcher.find()){
            res = "Movie ";
            int b = title.indexOf("Movie") + ("Movie").length();
            int fin = (title.contains("English"))
                    ? title.lastIndexOf("English")
                    : title.length();
            if (fin <= b) fin = title.length(); //if the actual series title contains english
            if (fin == b) return res; //title ends with movie
            boolean numberFound = false;

            for (b=b ; b < title.length(); b++) {
                if (!numberFound) {
                    try {
                        int eNum = Integer.parseInt(String.valueOf(title.charAt(b)));
                        //if here then char was an int
                        res = res.concat(String.valueOf(title.charAt(b)));
                    } catch (NumberFormatException e) {
                        if (!(String.valueOf(title.charAt(b)).matches("[ :]"))) {
                            break;
                        }
                    }
                }
            }
            res = res.concat("\n" + title.substring(b, fin-1));
            return res;
        }


        return null;
    }

}
