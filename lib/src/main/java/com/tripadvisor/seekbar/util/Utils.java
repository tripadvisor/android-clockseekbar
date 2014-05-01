package com.tripadvisor.seekbar.util;

import android.content.Context;
import android.graphics.Typeface;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by ksarmalkar on 4/30/14.
 */
public class Utils {

    public static final DateTimeFormatter SIMPLE_DATE_FORMAT_AM_PM = DateTimeFormat.forPattern("h");
    public static final DateTimeFormatter SIMPLE_DATE_FORMAT_MERIDIAN = DateTimeFormat.forPattern("a");
    public static final DateTimeFormatter SIMPLE_DATE_FORMAT_HOURS = DateTimeFormat.forPattern("H");

    @SuppressWarnings("PublicInnerClass")
    public enum FontType {
        LIGHT,
        MEDIUM,
        REGULAR,
        BOLD
    }

    private static final Map<FontType, String> sFontMap = new EnumMap<FontType, String>(FontType.class);

    static {
        sFontMap.put(FontType.LIGHT, "Roboto-Light.ttf");
        sFontMap.put(FontType.MEDIUM, "Roboto-Medium.ttf");
        sFontMap.put(FontType.REGULAR, "Roboto-Regular.ttf");
        sFontMap.put(FontType.BOLD, "Roboto-Bold.ttf");
    }

    /**
     * Cache for loaded Roboto typefaces.
     */
    private static final Map<FontType, Typeface> sTypefaceCache = new EnumMap<FontType, Typeface>(FontType.class);

    /**
     * Creates and returns Roboto typeface and caches it.
     *
     * @param context  {@link android.content.Context} Context that will obtain resources.
     * @param fontType {@link com.tripadvisor.seekbar.util.Utils.FontType} LIGHT, MEDIUM, REGULAR, or BOLD
     * @return Returns the {@link android.graphics.Typeface} corresponding to the {@link com.tripadvisor.seekbar.util.Utils.FontType}
     */
    public static Typeface getRobotoTypeface(Context context, FontType fontType) {
        String fontPath = sFontMap.get(fontType);
        if (!sTypefaceCache.containsKey(fontType)) {
            sTypefaceCache.put(fontType, Typeface.createFromAsset(context.getAssets(), fontPath));
        }
        return sTypefaceCache.get(fontType);
    }
}
