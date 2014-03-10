package com.xlythe.engine.theme;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.SparseArray;

public class Theme {
    public static final String COLOR = "color";
    public static final String RAW = "raw";
    public static final String DRAWABLE = "drawable";
    public static final String STRING = "string";
    private static String PACKAGE_NAME;
    private static SparseArray<Theme.Res> RES_MAP;
    private static final Map<String, Typeface> TYPEFACE_MAP = new HashMap<String, Typeface>();
    private static final Map<String, Drawable> DRAWABLE_MAP = new HashMap<String, Drawable>();
    private static final Map<String, Integer> COLOR_MAP = new HashMap<String, Integer>();
    private static final Map<String, ColorStateList> COLOR_STATE_LIST_MAP = new HashMap<String, ColorStateList>();

    public static class Res {
        private final String type;
        private final String name;

        private Res(String type, String name) {
            this.type = type;
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

    }

    @SuppressWarnings("rawtypes")
    public static void buildResourceMap(Class color, Class drawable, Class raw) {
        RES_MAP = new SparseArray<Theme.Res>();
        if(color != null) {
            for(Field f : color.getFields()) {
                Res r = new Res(COLOR, f.getName());
                try {
                    RES_MAP.put(f.getInt(null), r);
                }
                catch(IllegalArgumentException e) {
                    e.printStackTrace();
                }
                catch(IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        if(drawable != null) {
            for(Field f : drawable.getFields()) {
                Res r = new Res(DRAWABLE, f.getName());
                try {
                    RES_MAP.put(f.getInt(null), r);
                }
                catch(IllegalArgumentException e) {
                    e.printStackTrace();
                }
                catch(IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        if(raw != null) {
            for(Field f : raw.getFields()) {
                Res r = new Res(RAW, f.getName());
                try {
                    RES_MAP.put(f.getInt(null), r);
                }
                catch(IllegalArgumentException e) {
                    e.printStackTrace();
                }
                catch(IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void clear() {
        TYPEFACE_MAP.clear();
        DRAWABLE_MAP.clear();
        COLOR_MAP.clear();
        COLOR_STATE_LIST_MAP.clear();
    }

    public static Context getThemeContext(Context context) {
        try {
            return context.createPackageContext(getPackageName(), Context.CONTEXT_INCLUDE_CODE + Context.CONTEXT_IGNORE_SECURITY);
        }
        catch(NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Grabs the Resources from packageName
     * */
    public static Resources getResources(Context context) {
        try {
            return context.getPackageManager().getResourcesForApplication(getPackageName());
        }
        catch(NameNotFoundException e) {
            e.printStackTrace();
            return context.getResources();
        }
    }

    /**
     * Gets id from theme apk
     * */
    public static int getId(Context context, String type, String name) {
        return getResources(context).getIdentifier(name, type, getPackageName());
    }

    /**
     * Gets string from theme apk
     * */
    public static String getString(Context context, int resId) {
        return getString(context, Theme.get(resId));
    }

    /**
     * Gets string from theme apk
     * */
    public static String getString(Context context, Res res) {
        return getString(context, res.getName());
    }

    /**
     * Gets string from theme apk
     * */
    public static String getString(Context context, String name) {
        int id = getId(context, STRING, name);
        if(id == 0) return null;
        return getResources(context).getString(id);
    }

    /**
     * Gets drawable from theme apk
     * */
    public static Drawable getDrawable(Context context, int resId) {
        return getDrawable(context, Theme.get(resId));
    }

    /**
     * Gets drawable from theme apk
     * */
    public static Drawable getDrawable(Context context, Res res) {
        return getDrawable(context, res.getName());
    }

    /**
     * Gets drawable from theme apk
     * */
    public static Drawable getDrawable(Context context, String name) {
        if(DRAWABLE_MAP.containsKey(getKey(context) + "_" + name)) {
            return DRAWABLE_MAP.get(getKey(context) + "_" + name).getConstantState().newDrawable();
        }
        int id = getId(context, DRAWABLE, name);
        if(id == 0) {
            id = context.getResources().getIdentifier(name, DRAWABLE, context.getPackageName());
            if(id != 0) {
                DRAWABLE_MAP.put(getKey(context) + "_" + name, context.getResources().getDrawable(id));
                return DRAWABLE_MAP.get(getKey(context) + "_" + name);
            }
            else return null;
        }
        DRAWABLE_MAP.put(getKey(context) + "_" + name, getResources(context).getDrawable(id));
        return DRAWABLE_MAP.get(getKey(context) + "_" + name);
    }

    /**
     * Gets color from theme apk
     * */
    public static int getColor(Context context, int resId) {
        return getColor(context, Theme.get(resId));
    }

    /**
     * Gets color from theme apk
     * */
    public static int getColor(Context context, Res res) {
        return getColor(context, res.getName());
    }

    /**
     * Gets color from theme apk
     * */
    public static int getColor(Context context, String name) {
        if(COLOR_MAP.containsKey(getKey(context) + "_" + name)) {
            return COLOR_MAP.get(getKey(context) + "_" + name);
        }
        int id = getId(context, COLOR, name);
        if(id == 0) {
            id = context.getResources().getIdentifier(name, COLOR, context.getPackageName());
            COLOR_MAP.put(getKey(context) + "_" + name, context.getResources().getColor(id));
            return COLOR_MAP.get(getKey(context) + "_" + name);
        }
        COLOR_MAP.put(getKey(context) + "_" + name, getResources(context).getColor(id));
        return COLOR_MAP.get(getKey(context) + "_" + name);
    }

    /**
     * Gets color from theme apk
     * */
    public static ColorStateList getColorStateList(Context context, int resId) {
        return getColorStateList(context, Theme.get(resId));
    }

    /**
     * Gets color from theme apk
     * */
    public static ColorStateList getColorStateList(Context context, Res res) {
        return getColorStateList(context, res.getName());
    }

    /**
     * Gets color from theme apk
     * */
    public static ColorStateList getColorStateList(Context context, String name) {
        if(COLOR_STATE_LIST_MAP.containsKey(getKey(context) + "_" + name)) {
            return COLOR_STATE_LIST_MAP.get(getKey(context) + "_" + name);
        }
        int id = getId(context, COLOR, name);
        if(id == 0) {
            id = context.getResources().getIdentifier(name, COLOR, context.getPackageName());
            COLOR_STATE_LIST_MAP.put(getKey(context) + "_" + name, context.getResources().getColorStateList(id));
            return COLOR_STATE_LIST_MAP.get(getKey(context) + "_" + name);
        }
        COLOR_STATE_LIST_MAP.put(getKey(context) + "_" + name, getResources(context).getColorStateList(id));
        return COLOR_STATE_LIST_MAP.get(getKey(context) + "_" + name);
    }

    /**
     * Gets android theme from theme apk. Can be 0 (no theme).
     * */
    public static int getTheme(Context context) {
        int id = getId(context, "string", "app_theme");
        if(id == 0) return 0;

        String fieldName = getResources(context).getString(id).replace(".", "_");
        try {
            Field field = android.R.style.class.getField(fieldName);
            return field.getInt(null);
        }
        catch(RuntimeException e) {
            e.printStackTrace();
        }
        catch(NoSuchFieldException e) {
            e.printStackTrace();
        }
        catch(IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Gets android theme from theme apk. Can be 0 (no theme). This is for apps that want an actionbar in their Settings but not in their main app.
     * */
    public static int getSettingsTheme(Context context) {
        int id = getId(context, "string", "app_settings_theme");
        if(id == 0) return 0;

        String fieldName = getResources(context).getString(id).replace(".", "_");
        try {
            Field field = android.R.style.class.getField(fieldName);
            return field.getInt(null);
        }
        catch(RuntimeException e) {
            e.printStackTrace();
        }
        catch(NoSuchFieldException e) {
            e.printStackTrace();
        }
        catch(IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Returns whether the theme is light or dark. WARNING: Assumes dark if no resource is found.
     * */
    public static boolean isLightTheme(Context context) {
        int id = getId(context, "string", "app_theme");
        if(id != 0) {
            String fieldName = getResources(context).getString(id).replace(".", "_");
            return fieldName.toLowerCase(Locale.US).contains("light");
        }
        else {
            return false;
        }
    }

    public static String getPackageName() {
        return PACKAGE_NAME;
    }

    public static void setPackageName(String packageName) {
        PACKAGE_NAME = packageName;
    }

    public static Res get(int resId) {
        return RES_MAP.get(resId);
    }

    public static String getSoundPath(Context context, Res res) {
        int id = getId(context, res.getType(), res.getName());
        if(id == 0) {
            id = context.getResources().getIdentifier(res.getName(), res.getType(), context.getPackageName());
            return "android.resource://" + context.getPackageName() + "/" + id;
        }
        return "android.resource://" + getPackageName() + "/" + id;
    }

    public static int getSound(Context context, SoundPool soundPool, Res res) {
        int id = getId(context, res.getType(), res.getName());
        if(id == 0) {
            id = context.getResources().getIdentifier(res.getName(), res.getType(), context.getPackageName());
            return soundPool.load(context, id, 1);
        }
        return soundPool.load(getThemeContext(context), id, 1);
    }

    public static long getDurationOfSound(Context context, Theme.Res res) {
        int millis = 0;
        MediaPlayer mp = new MediaPlayer();
        try {
            AssetFileDescriptor afd;
            int id = getId(context, res.getType(), res.getName());
            if(id == 0) {
                id = context.getResources().getIdentifier(res.getName(), res.getType(), context.getPackageName());
                afd = context.getResources().openRawResourceFd(id);
            }
            afd = getThemeContext(context).getResources().openRawResourceFd(id);
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mp.prepare();
            millis = mp.getDuration();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            mp.release();
            mp = null;
        }
        return millis;
    }

    public static Typeface getFont(Context context) {
        if(TYPEFACE_MAP.containsKey(getKey(context))) {
            return TYPEFACE_MAP.get(getKey(context));
        }
        AssetManager am = getResources(context).getAssets();
        try {
            String[] assets = am.list("");
            for(String s : assets) {
                if(s.startsWith("font")) {
                    Typeface t = null;
                    try {
                        // Try/catch for broken fonts
                        t = Typeface.createFromAsset(am, s);
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }

                    TYPEFACE_MAP.put(getKey(context), t);
                    return TYPEFACE_MAP.get(getKey(context));
                }
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        am = context.getResources().getAssets();
        try {
            String[] assets = am.list("");
            for(String s : assets) {
                if(s.startsWith("font")) {
                    Typeface t = null;
                    try {
                        // Try/catch for broken fonts
                        t = Typeface.createFromAsset(am, s);
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }

                    TYPEFACE_MAP.put(getKey(context), t);
                    return TYPEFACE_MAP.get(getKey(context));
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        TYPEFACE_MAP.put(getKey(context), null);
        return TYPEFACE_MAP.get(getKey(context));
    }

    /**
     * Returns a list of installed apps that are registered as themes
     * */
    public static List<App> getApps(Context context) {
        LinkedList<App> apps = new LinkedList<App>();
        PackageManager manager = context.getPackageManager();

        Intent mainIntent = new Intent(context.getPackageName() + ".THEME", null);

        final List<ResolveInfo> infos;
        try {
            infos = manager.queryIntentActivities(mainIntent, 0);
        }
        catch(Exception e) {
            e.printStackTrace();
            return apps;
        }

        for(ResolveInfo info : infos) {
            App app = new App();
            apps.add(app);

            app.setName(info.loadLabel(manager).toString());
            app.setPackageName(info.activityInfo.applicationInfo.packageName);
        }
        return apps;
    }

    private static String getKey(Context context) {
        return context.getPackageName() + "_" + getPackageName();
    }
}
