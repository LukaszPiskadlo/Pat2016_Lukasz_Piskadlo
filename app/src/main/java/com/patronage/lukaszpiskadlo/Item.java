package com.patronage.lukaszpiskadlo;

public class Item {
    private String title;
    private String desc;
    private String url;

    public Item(String title, String desc, String url) {
        this.title = title;
        this.desc = desc;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getUrl() {
        return url;
    }
}
