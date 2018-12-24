package com.baizhi.entity;

public class Poetry {
    private Integer id;
    private Integer poetId;
    private String content;
    private String title;
    private Poet poet;

    public Poetry() {
    }

    public Poetry(Integer id, Integer poetId, String content, String title, Poet poet) {
        this.id = id;
        this.poetId = poetId;
        this.content = content;
        this.title = title;
        this.poet = poet;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPoetId() {
        return poetId;
    }

    public void setPoetId(Integer poetId) {
        this.poetId = poetId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Poet getPoet() {
        return poet;
    }

    public void setPoet(Poet poet) {
        this.poet = poet;
    }

    @Override
    public String toString() {
        return "Poetry{" +
                "id=" + id +
                ", poetId=" + poetId +
                ", content='" + content + '\'' +
                ", title='" + title + '\'' +
                ", poet=" + poet +
                '}';
    }
}
