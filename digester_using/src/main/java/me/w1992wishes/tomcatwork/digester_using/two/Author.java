package me.w1992wishes.tomcatwork.digester_using.two;

import org.apache.commons.digester3.annotations.rules.ObjectCreate;
import org.apache.commons.digester3.annotations.rules.SetNext;

import java.util.ArrayList;
import java.util.List;

/**
 * 用来存放books/book/author标签中的内容
 *
 * Created by w1992wishes
 * on 2018/2/9.
 */
@ObjectCreate(pattern = "books/book/author")
public class Author {
    private List<AuthorName> authorNames;

    public List<AuthorName> getAuthorNames() {
        return authorNames;
    }

    public void setAuthorNames(List<AuthorName> authorNames) {
        this.authorNames = authorNames;
    }

    @SetNext
    public void addAuthorName(AuthorName authorName){
        if (authorName == null){
            return;
        }
        if (this.authorNames == null){
            this.authorNames = new ArrayList<>();
        }
        this.authorNames.add(authorName);
    }
}
