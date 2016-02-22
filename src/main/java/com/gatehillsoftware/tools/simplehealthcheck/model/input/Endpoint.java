package com.gatehillsoftware.tools.simplehealthcheck.model.input;

/**
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class Endpoint {
    private String url;
    private CheckType check;
    private String name;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public CheckType getCheck() {
        return check;
    }

    public void setCheck(CheckType check) {
        this.check = check;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
