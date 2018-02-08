package me.w1992wishes.tomcatwork.simple_tomcat_07.loader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Resource {

    public Resource() {
    }

    public Resource(InputStream inputStream) {
        setContent(inputStream);
    }

    public Resource(byte[] binaryContent) {
        setContent(binaryContent);
    }
    
    /**
     * Binary content.
     */
    protected byte[] binaryContent = null;
    
    /**
     * Input stream.
     */
    protected InputStream inputStream = null;
    
    /**
     * Content accessor.
     * 
     * @return InputStream
     */
    public InputStream streamContent() throws IOException {
        if (binaryContent != null) {
            return new ByteArrayInputStream(binaryContent);
        }
        return inputStream;
    }
    
    /**
     * Content accessor.
     * 
     * @return binary content
     */
    public byte[] getContent() {
        return binaryContent;
    }

    /**
     * Content mutator.
     * 
     * @param inputStream New input stream
     */
    public void setContent(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * Content mutator.
     * 
     * @param binaryContent New bin content
     */
    public void setContent(byte[] binaryContent) {
        this.binaryContent = binaryContent;
    }
    
    
}