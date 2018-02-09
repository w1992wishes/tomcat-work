package me.w1992wishes.tomcatwork.digester_using.two;

import org.apache.commons.digester3.annotations.rules.BeanPropertySetter;
import org.apache.commons.digester3.annotations.rules.ObjectCreate;

/**
 * 用来存放books/book/byname/name标签中的内容
 *
 * Created by w1992wishes
 * on 2018/2/9.
 */
@ObjectCreate(pattern = "books/book/byname/name")
public class Name {
    //books/book/byname/name标签内容和Name的name属性映射
    @BeanPropertySetter(pattern = "books/book/byname/name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
