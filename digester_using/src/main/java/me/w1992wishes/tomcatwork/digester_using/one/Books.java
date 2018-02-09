package me.w1992wishes.tomcatwork.digester_using.one;

import java.util.ArrayList;
import java.util.List;

/**
 * 用来存放books/book标签集。
 *
 * Created by w1992wishes
 * on 2018/2/9.
 */
public class Books {
    private List<Book> bookList;

    public List<Book> getBookList() {
        return bookList;
    }

    public void setBookList(List<Book> bookList) {
        this.bookList = bookList;
    }

    public void addBook(Book book) {
        if (book == null) {
            return;
        }
        if (this.bookList == null) {
            this.bookList = new ArrayList<>();
        }
        this.bookList.add(book);
    }
}
