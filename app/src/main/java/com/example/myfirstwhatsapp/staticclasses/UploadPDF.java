package com.example.myfirstwhatsapp.staticclasses;

public class UploadPDF {
    private String name , url , key;

    public UploadPDF() {
    }

    public UploadPDF(String name, String url ,String key ) {
        this.name = name;
        this.url = url;
        this.key=key;

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}
