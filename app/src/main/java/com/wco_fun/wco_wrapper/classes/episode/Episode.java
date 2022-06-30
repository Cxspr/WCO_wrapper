package com.wco_fun.wco_wrapper.classes.episode;

import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Episode {
    protected String title;
    protected String src;
//    protected String abrTitle;
    protected int idx = 0;

    //constructors
    public Episode() { }

    public Episode(Element node) {
        if (node != null) {
            title = node.hasAttr("title")
                    ? node.attr("title")
                    : null;
            src = node.hasAttr("href")
                    ? node.attr("href")
                    : null;
            if (title.indexOf("Watch ") != -1) {
                title = title.substring(6);
            }
        }
    }



    // return true if series has a title and src
    public boolean isValid() {
        return !(title.isEmpty() || src.isEmpty());
    }

    public String getTitle() {return title;}
    public String getSrc() {return src;}
    public void setSrc(String src) {this.src = src;}
    public String getAbrTitle() {return genAbrTitle("");}
    public String getAbrTitle(String seriesName) {return genAbrTitle(seriesName);}
    public int getIdx() {return idx;}
    public void setIdx(int idx) {this.idx = idx;}

    public String genAbrTitle() { return genAbrTitle(""); }

    public String genAbrTitle(String seriesName) {
        boolean  seriesNameGiven = !seriesName.equals("");
        String res = "";
        Pattern pattern;
        Matcher matcher;

        if (seriesNameGiven) {
            //{seriesName} ... Arc Episode x: movie title //main culprit here is demon slayer
            pattern = Pattern.compile(seriesName + "[: ]*.* Arc Episode [0-9]*", Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(title);
            if (matcher.find()){
                res = title.substring(matcher.start(), matcher.end());
                int padding = (title.indexOf(seriesName + ": ") == -1)
                        ? 1
                        : 2;
                res = res.substring((seriesName.length() + padding - 1));
                res = res.replace("Episode", "Ep.");
                return res;
            }
        }
        //Season xyz Episode xyz
        pattern = Pattern.compile(".*Season [0-9]* Episode [0-9]*", Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(title);
        if (matcher.find()){
            int a = title.lastIndexOf("Season "),b = title.indexOf("Episode ");
            a+=("Season ").length();
            res = "S";
            String sNum = title.substring(a,b-1);
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
        pattern = Pattern.compile("Movie[ :[0-9]]*", Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(title);
        if (matcher.find()){
            res = title.substring(matcher.start(), matcher.end());
            int b = title.indexOf("Movie") + res.length();
            int fin = (title.contains("English"))
                    ? title.lastIndexOf("English")
                    : title.length();
            if (fin <= b) fin = title.length(); //if the actual series title contains english
            if (fin == b) return "Movie"; //title ends with movie
            boolean numberFound = false;

            res = res.concat(title.substring(b, fin-1));
            return res;
        }

        return null;
    }
}
