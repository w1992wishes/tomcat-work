package me.w1992wishes.tomcatwork.simple_tomcat_06.util;

import java.util.*;

/**
 * Created by w1992wishes
 * on 2018/1/25.
 */
public final class Enumerator implements Enumeration {

    public Enumerator(Collection collection) {
        this(collection.iterator());
    }

    public Enumerator(Collection collection, boolean clone) {
        this(collection.iterator(), clone);
    }

    public Enumerator(Iterator iterator) {
        super();
        this.iterator = iterator;
    }

    public Enumerator(Iterator iterator, boolean clone) {
        super();
        if (!clone) {
            this.iterator = iterator;
        } else {
            List list = new LinkedList();
            while(iterator.hasNext()) {
                list.add(iterator.next());
            }
            this.iterator = list.iterator();
        }
    }

    public Enumerator(Map map) {
        this(map.values().iterator());
    }

    public Enumerator(Map map, boolean clone) {
        this(map.values().iterator(), clone);
    }

    private Iterator iterator = null;

    public boolean hasMoreElements() {
        return (iterator.hasNext());
    }

    public Object nextElement() throws NoSuchElementException {
        return (iterator.next());
    }

}
