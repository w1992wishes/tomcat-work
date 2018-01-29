package me.w1992wishes.tomcatwork.simple_tomcat_06.exception;

public final class LifecycleException extends Exception {


    //------------------------------------------------------------ Constructors


    /**
     * Construct a new LifecycleException with no other information.
     */
    public LifecycleException() {

        this(null, null);

    }


    /**
     * Construct a new LifecycleException for the specified message.
     *
     * @param message Message describing this exception
     */
    public LifecycleException(String message) {

        this(message, null);

    }


    /**
     * Construct a new LifecycleException for the specified throwable.
     *
     * @param throwable Throwable that caused this exception
     */
    public LifecycleException(Throwable throwable) {

        this(null, throwable);

    }


    /**
     * Construct a new LifecycleException for the specified message
     * and throwable.
     *
     * @param message Message describing this exception
     * @param throwable Throwable that caused this exception
     */
    public LifecycleException(String message, Throwable throwable) {

        super();
        this.message = message;
        this.throwable = throwable;

    }


    //------------------------------------------------------ Instance Variables


    /**
     * The error message passed to our constructor (if any)
     */
    protected String message = null;


    /**
     * The underlying exception or error passed to our constructor (if any)
     */
    protected Throwable throwable = null;


    //---------------------------------------------------------- Public Methods


    /**
     * Returns the message associated with this exception, if any.
     */
    public String getMessage() {

        return (message);

    }


    /**
     * Returns the throwable that caused this exception, if any.
     */
    public Throwable getThrowable() {

        return (throwable);

    }


    /**
     * Return a formatted string that describes this exception.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("LifecycleException:  ");
        if (message != null) {
            sb.append(message);
            if (throwable != null) {
                sb.append(":  ");
            }
        }
        if (throwable != null) {
            sb.append(throwable.toString());
        }
        return (sb.toString());

    }


}