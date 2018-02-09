package me.w1992wishes.tomcatwork.digester_using.two;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.annotations.FromAnnotationsRuleModule;
import org.apache.commons.digester3.binder.DigesterLoader;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.digester3.binder.DigesterLoader.newLoader;

/**
 * 测试Digester3注解映射XML和Java对象
 *
 * Created by w1992wishes
 * on 2018/2/9.
 */
public class Test {
    public static Books readBooks(File xmlPath, Class<?> XmlClazz) throws IOException, SAXException {
        Digester digester = getLoader(XmlClazz).newDigester();
        return digester.parse(xmlPath);
    }

    public static DigesterLoader getLoader(final Class<?> XmlClazz) {
        return newLoader(new FromAnnotationsRuleModule() {
            @Override
            protected void configureRules() {
                bindRulesFrom(XmlClazz);
            }
        });
    }

    public static void main(String[] args) throws IOException, SAXException {
        String baseDir = System.getProperty("user.dir");
        File xmlFile = new File(baseDir + "/digester_using/src/main/resources/books.xml");
        Books books = readBooks(xmlFile, Books.class);
    }
}
