package com.xlythe.engine.theme;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.Switch;

public class ThemedSwitch extends Switch {
    public ThemedSwitch(Context context) {
        super(context);
        setup(context, null);
    }

    public ThemedSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs);
    }

    public ThemedSwitch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup(context, attrs);
    }

    @TargetApi(21)
    public ThemedSwitch(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup(context, attrs);
    }

    private void setup(Context context, AttributeSet attrs) {
        // Get font
        setDefaultFont();

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.theme);
            if (a != null) {
                // Get button
                setButtonDrawable(Theme.get(context, a.getResourceId(R.styleable.theme_button, 0)));

                // Get background
                setBackground(Theme.get(context, a.getResourceId(R.styleable.theme_themeBackground, 0)));

                // Get custom font. Note that in v26, /res/font was added so we need to play some games.
                if (Build.VERSION.SDK_INT >= 21) {
                    switch (a.getType(R.styleable.theme_themeFont)) {
                        case TypedValue.TYPE_ATTRIBUTE:
                            setFont(Theme.get(context, a.getResourceId(R.styleable.theme_themeFont, 0)));
                            break;
                        case TypedValue.TYPE_STRING:
                            setFont(a.getString(R.styleable.theme_themeFont));
                            break;
                    }
                }

                a.recycle();
            }
        }
    }

    public void setDefaultFont() {
        Typeface t = Theme.getFont(getContext());
        if (t != null) {
            setTypeface(t);
        }
    }

    public void setFont(@Nullable Theme.Res res) {
        if (res != null) {
            if (Theme.FONT.equals(res.getType())) {
                Typeface t = Theme.getFont(getContext(), res);
                if (t != null) {
                    setTypeface(t);
                }
            }
        }
    }

    @Deprecated
    public void setFont(@Nullable String font) {
        if (font != null) {
            Typeface t = Theme.getFont(getContext(), font);
            if (t != null) {
                setTypeface(t);
            }
        }
    }

    public void setButtonDrawable(@Nullable Theme.Res res) {
        if (res != null) {
            if (Theme.DRAWABLE.equals(res.getType())) {
                setButtonDrawable(Theme.getDrawable(getContext(), res.getName()));
            }
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public void setBackground(@Nullable Theme.Res res) {
        if (res != null) {
            if (Theme.COLOR.equals(res.getType())) {
                setBackgroundColor(Theme.getColor(getContext(), res.getName()));
            } else if (Theme.DRAWABLE.equals(res.getType())) {
                if (Build.VERSION.SDK_INT < 16) {
                    setBackgroundDrawable(Theme.getDrawable(getContext(), res.getName()));
                } else {
                    setBackground(Theme.getDrawable(getContext(), res.getName()));
                }
            }
        }
    }
}