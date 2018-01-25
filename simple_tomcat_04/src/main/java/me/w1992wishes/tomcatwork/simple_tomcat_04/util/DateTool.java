package me.w1992wishes.tomcatwork.simple_tomcat_04.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by w1992wishes
 * on 2018/1/25.
 */
public class DateTool {
    /** US locale - all HTTP dates are in english
     */
    public final static Locale LOCALE_US = Locale.US;

    /** GMT timezone - all HTTP dates are on GMT
     */
    public final static TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");

    /** format for RFC 1123 date string -- "Sun, 06 Nov 1994 08:49:37 GMT"
     */
    public final static String RFC1123_PATTERN =
            "EEE, dd MMM yyyyy HH:mm:ss z";

    // format for RFC 1036 date string -- "Sunday, 06-Nov-94 08:49:37 GMT"
    private final static String rfc1036Pattern =
            "EEEEEEEEE, dd-MMM-yy HH:mm:ss z";

    // format for C asctime() date string -- "Sun Nov  6 08:49:37 1994"
    private final static String asctimePattern =
            "EEE MMM d HH:mm:ss yyyyy";

    /** Pattern used for old cookies
     */
    public final static String OLD_COOKIE_PATTERN = "EEE, dd-MMM-yyyy HH:mm:ss z";

    /** DateFormat to be used to format dates
     */
    public final static DateFormat rfc1123Format =
            new SimpleDateFormat(RFC1123_PATTERN, LOCALE_US);

    /** DateFormat to be used to format old netscape cookies
     */
    public final static DateFormat oldCookieFormat =
            new SimpleDateFormat(OLD_COOKIE_PATTERN, LOCALE_US);

    public final static DateFormat rfc1036Format =
            new SimpleDateFormat(rfc1036Pattern, LOCALE_US);

    public final static DateFormat asctimeFormat =
            new SimpleDateFormat(asctimePattern, LOCALE_US);

    static {
        rfc1123Format.setTimeZone(GMT_ZONE);
        oldCookieFormat.setTimeZone(GMT_ZONE);
        rfc1036Format.setTimeZone(GMT_ZONE);
        asctimeFormat.setTimeZone(GMT_ZONE);
    }
}
