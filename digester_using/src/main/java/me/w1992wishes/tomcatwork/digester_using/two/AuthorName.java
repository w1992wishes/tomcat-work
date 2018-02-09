package me.w1992wishes.tomcatwork.digester_using.two;

import org.apache.commons.digester3.annotations.rules.BeanPropertySetter;
import org.apache.commons.digester3.annotations.rules.ObjectCreate;

/**
 * 用来存放books/book/author/name标签中的内容
 *
 * Created by w1992wishes
 * on 2018/2/9.
 */
@ObjectCreate(pattern = "books/book/author/name")
public class AuthorName {
    @BeanPropertySetter(pattern = "books/book/author/name")
    private String authorName;

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
}
