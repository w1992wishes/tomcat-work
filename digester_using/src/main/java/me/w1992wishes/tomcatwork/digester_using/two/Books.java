package me.w1992wishes.tomcatwork.digester_using.two;

import org.apache.commons.digester3.annotations.rules.ObjectCreate;
import org.apache.commons.digester3.annotations.rules.SetNext;

import java.util.ArrayList;
import java.util.List;

/**
 * 用来存放books/book标签集
 *
 * Created by w1992wishes
 * on 2018/2/9.
 */
@ObjectCreate(pattern = "books")
public class Books {
    private List<Book> bookList;

    public List<Book> getBookList() {
        return bookList;
    }

    public void setBookList(List<Book> bookList) {
        this.bookList = bookList;
    }

    //将books/book标签的内容对象添加到Books对象中
    @SetNext
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
