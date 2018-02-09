package me.w1992wishes.tomcatwork.digester_using.one;

/**
 * 用来存放books/book标签中的内容
 *
 * Created by w1992wishes
 * on 2018/2/9.
 */
public class Book {
    private Author author;
    private Byname byname;
    private String creationDate;
    private String literaryStyle;
    private String name;

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Byname getByname() {
        return byname;
    }

    public void setByname(Byname byname) {
        this.byname = byname;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getLiteraryStyle() {
        return literaryStyle;
    }

    public void setLiteraryStyle(String literaryStyle) {
        this.literaryStyle = literaryStyle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addAuthor(Author author){
        this.author = author;
    }

    public void addByname(Byname byname){
        this.byname = byname;
    }
}
