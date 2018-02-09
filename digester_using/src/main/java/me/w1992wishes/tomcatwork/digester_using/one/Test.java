package me.w1992wishes.tomcatwork.digester_using.one;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

/**
 * Created by w1992wishes
 * on 2018/2/9.
 */
public class Test {

    public static Books parseXml(File xmlFile) throws IOException, SAXException {
        Digester digester = new Digester();
        digester.setValidating(false);

        //跟标签/books和Books对象映射
        digester.addObjectCreate("books", Books.class);

        //标签/books/book和Book对象映射
        digester.addObjectCreate("books/book", Book.class);

        //将/books/book标签的所有属性映射到Book对象的属性上，在这里映射的是/books/book标签的name属性。
        digester.addSetProperties("books/book");
        //或者使用下面这种方式代替
        // digester.addSetProperties("books/book");这种方式要求标签属性名和对象中的字段要保持命名一致才可以映射上。
        // digester.addSetProperties("books/book","name","name");

        //标签books/book/creationDate，和Book对象的creationDate属性映射
        digester.addBeanPropertySetter("books/book/creationDate", "creationDate");
        //标签books/book/literaryStyle，和Book对象的literaryStyle属性映射
        digester.addBeanPropertySetter("books/book/literaryStyle", "literaryStyle");

        //标签books/book/author和Author对象映射
        digester.addObjectCreate("books/book/author", Author.class);

        //标签books/book/author/name和AuthorName对象映射
        digester.addObjectCreate("books/book/author/name", AuthorName.class);
        //标签books/book/author/name，和AuthorName对象的name属性映射
        digester.addBeanPropertySetter("books/book/author/name", "name");

        //标签books/book/byname和Byname对象映射
        digester.addObjectCreate("books/book/byname", Byname.class);
        //标签books/book/byname/name和Name对象映射
        digester.addObjectCreate("books/book/byname/name", Name.class);
        //标签books/book/byname/name，和Byname对象的name属性映射
        digester.addBeanPropertySetter("books/book/byname/name", "name");

        //把Book标签对象添加到Book对象中，需要保证Books对象中有addBooks该方法，用于添加装载XML标签内容后的对象信息
        digester.addSetNext("books/book", "addBook");
        //把Author标签对象和Byname标签对象添加到Book对象中，需要保证Book对象中有addAuthor和addByname方法，用于添加装载XML标签内容后的对象信息
        digester.addSetNext("books/book/author", "addAuthor");
        digester.addSetNext("books/book/byname", "addByname");

        //把Name标签对象添加到Byname对象中，需要保证Byname对象中有aaddName方法（命名任意，只需要对应上即可），用于添加装载XML标签内容后的对象信息
        digester.addSetNext("books/book/byname/name", "addName");
        //把AuthorName标签对象添加到Author对象中，需要保证Author对象中有addAuthorName方法（命名任意，只需要对应上即可），用于添加装载XML标签内容后的对象信息
        digester.addSetNext("books/book/author/name", "addAuthorName");
        Object obj = digester.parse(xmlFile);
        if (obj instanceof Books) {
            return (Books) obj;
        }
        return null;
    }

    public static void main(String[] args) throws IOException, SAXException {
        String baseDir = System.getProperty("user.dir");
        File xmlFile = new File(baseDir + "/digester_using/src/main/resources/books.xml");
        Books books = parseXml(xmlFile);
    }
}
