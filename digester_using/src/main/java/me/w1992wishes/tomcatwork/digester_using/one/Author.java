package me.w1992wishes.tomcatwork.digester_using.one;

import java.util.ArrayList;
import java.util.List;

/**
 * 用来存放books/book/author/name标签集合
 *
 * Created by w1992wishes
 * on 2018/2/9.
 */
public class Author {
    private List<AuthorName> authorNames;

    public List<AuthorName> getAuthorNames() {
        return authorNames;
    }

    public void setAuthorNames(List<AuthorName> authorNames) {
        this.authorNames = authorNames;
    }

    public void addAuthorName(AuthorName name) {
        if (name == null) {
            return;
        }
        if (this.authorNames == null) {
            this.authorNames = new ArrayList<>();
        }
        this.authorNames.add(name);
    }
}
