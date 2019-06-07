package com.example.administrator.materiatest;

public class Label {
    private int id;
    private String title;
    static int range=2;
    public Label() {
        id=range;
        range++;
    }

    @Override
    public String toString() {
        return title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Label)) {
            return false;
        }
        Label book = (Label) o;
        return book.getId()==id;
    }


}
