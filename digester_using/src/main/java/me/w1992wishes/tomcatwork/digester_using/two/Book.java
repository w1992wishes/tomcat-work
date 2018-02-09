package me.w1992wishes.tomcatwork.digester_using.two;

import org.apache.commons.digester3.annotations.rules.BeanPropertySetter;
import org.apache.commons.digester3.annotations.rules.ObjectCreate;
import org.apache.commons.digester3.annotations.rules.SetNext;
import org.apache.commons.digester3.annotations.rules.SetProperty;

/**
 * 用来存放books/book标签中的内容
 *
 * Created by w1992wishes
 * on 2018/2/9.
 */
@ObjectCreate(pattern = "books/book")
public class Book {
    private Author author;
    private Byname byname;

    //books/book/creationDate标签内容和Book的creationDate属性映射
    @BeanPropertySetter(pattern = "books/book/creationDate")
    private String creationDate;
    //books/book/literaryStyle标签内容和Book的literaryStyle属性映射
    @BeanPropertySetter(pattern = "books/book/literaryStyle")
    private String literaryStyle;
    //将/books/book标签的所有属性映射到Book对象的属性上，在这里映射的是/books/book标签的name属性。
    @SetProperty(pattern = "books/book")
    private String name;

    //将books/book/byname标签的内容对象添加到Book对象中
    @SetNext
    public void addByname(Byname byname){
        this.byname = byname;
    }

    //将books/book/author标签的内容对象添加到Book对象中
    @SetNext
    public void addAuthor(Author author){
        this.author = author;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public Byname getByname() {
        return byname;
    }

    public void setByname(Byname byname) {
        this.byname = byname;
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
}
