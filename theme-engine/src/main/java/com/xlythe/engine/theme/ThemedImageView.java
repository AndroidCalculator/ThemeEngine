package com.xlythe.engine.theme;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.AttributeSet;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.AppCompatImageView;

public class ThemedImageView extends AppCompatImageView {
    public ThemedImageView(Context context) {
        super(context);
        setup(context, null, 0, 0);
    }

    public ThemedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs, 0, 0);
    }

    public ThemedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public ThemedImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        setup(context, attrs, defStyleAttr, defStyleRes);
    }

    private void setup(
            Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (attrs != null) {
            TypedArray a =
                    context.obtainStyledAttributes(attrs, R.styleable.theme, defStyleAttr, defStyleRes);
            if (a != null) {
                // Get image
                setImageDrawable(Theme.get(context, a.getResourceId(R.styleable.theme_themedSrc, 0)));

                // Get tint
                setTint(Theme.get(context, a.getResourceId(R.styleable.theme_themedTint, 0)));

                // Get background
                setBackground(Theme.get(context, a.getResourceId(R.styleable.theme_themedBackground, 0)));

                a.recycle();
            }
        }
    }

    @UiThread
    public void setImageDrawable(@Nullable Theme.Res res) {
        if (res != null) {
            if (Theme.DRAWABLE.equals(res.getType())) {
                setImageDrawable(Theme.getDrawable(getContext(), res));
            }
        }
    }

    @UiThread
    public void setTint(@Nullable Theme.Res res) {
        if (res != null) {
            if (Theme.COLOR.equals(res.getType())) {
                setColorFilter(Theme.getColor(getContext(), res), PorterDuff.Mode.MULTIPLY);
            }
        }
    }

    @UiThread
    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public void setBackground(@Nullable Theme.Res res) {
        if (res != null) {
            if (Theme.COLOR.equals(res.getType())) {
                setBackgroundColor(Theme.getColor(getContext(), res));
            } else if (Theme.DRAWABLE.equals(res.getType())) {
                if (Build.VERSION.SDK_INT < 16) {
                    setBackgroundDrawable(Theme.getDrawable(getContext(), res));
                } else {
                    setBackground(Theme.getDrawable(getContext(), res));
                }
            }
        }
    }

    @UiThread
    public void setWidth(@Nullable Theme.Res res) {
        if (res != null) {
            if (Theme.DIMEN.equals(res.getType())) {
                getLayoutParams().width = (int) Theme.getDimen(getContext(), res);
                requestLayout();
            }
        }
    }

    @UiThread
    public void setHeight(@Nullable Theme.Res res) {
        if (res != null) {
            if (Theme.DIMEN.equals(res.getType())) {
                getLayoutParams().height = (int) Theme.getDimen(getContext(), res);
                requestLayout();
            }
        }
    }
}