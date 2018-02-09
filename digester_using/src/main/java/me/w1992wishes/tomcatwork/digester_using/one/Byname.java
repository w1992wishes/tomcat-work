package me.w1992wishes.tomcatwork.digester_using.one;

import java.util.ArrayList;
import java.util.List;

/**
 * Byname.java，用来存放books/book/byname/name标签集合。
 *
 * Created by w1992wishes
 * on 2018/2/9.
 */
public class Byname {
    private List<Name> names;

    public List<Name> getNames() {
        return names;
    }

    public void setNames(List<Name> names) {
        this.names = names;
    }

    public void addName(Name name){
        if(name==null){
            return;
        }
        if(this.names==null){
            this.names = new ArrayList<>();
        }
        this.names.add(name);
    }
}
