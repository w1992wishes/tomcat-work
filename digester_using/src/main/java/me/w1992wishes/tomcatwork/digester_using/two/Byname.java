package me.w1992wishes.tomcatwork.digester_using.two;

import org.apache.commons.digester3.annotations.rules.ObjectCreate;
import org.apache.commons.digester3.annotations.rules.SetNext;

import java.util.ArrayList;
import java.util.List;

/**
 * 用来存放books/book/byname/name标签集合
 *
 * Created by w1992wishes
 * on 2018/2/9.
 */
@ObjectCreate(pattern = "books/book/byname")
public class Byname {
    private List<Name> names;

    public List<Name> getNames() {
        return names;
    }

    public void setNames(List<Name> names) {
        this.names = names;
    }

    //将books/book/byname/name标签的内容对象添加到Byname对象中
    @SetNext
    public void addName(Name name){
        if (name == null){
            return;
        }
        if (this.names == null){
            this.names = new ArrayList<>();
        }
        this.names.add(name);
    }
}
