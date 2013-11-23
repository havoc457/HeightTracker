package edu.berkeley.SouthsideSeniors.HeightTracker;

import java.lang.reflect.Field;

import android.content.Context;
import android.graphics.Typeface;

public final class FontsOverride {

    public static void setDefaultFont(Context context, String staticTypefaceFieldName, Typeface face) {
        replaceFont(staticTypefaceFieldName, face);
    }

    protected static void replaceFont(String staticTypefaceFieldName,
            final Typeface newTypeface) {
        try {
            final Field StaticField = Typeface.class
                    .getDeclaredField(staticTypefaceFieldName);
            StaticField.setAccessible(true);
            StaticField.set(null, newTypeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
