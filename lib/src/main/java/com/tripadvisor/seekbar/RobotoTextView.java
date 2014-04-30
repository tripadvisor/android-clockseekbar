package com.tripadvisor.seekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.tripadvisor.seekbar.util.Utils;

import static com.tripadvisor.seekbar.util.Utils.FontType.*;
import static com.tripadvisor.seekbar.util.Utils.getRobotoTypeface;


/**
 * Created by ksarmalkar on 2/24/14.
 */
public class RobotoTextView extends TextView {

    private Utils.FontType mFontType;
    private Context mContext;

    public RobotoTextView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        this.mContext = context;

        if(!isInEditMode()){
            TypedArray attributesArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RobotoTextView, 0, 0);
            try{
                mFontType = values()[attributesArray.getInteger(R.styleable.RobotoTextView_fontType, 0)];
                setFontType(mFontType);
                // Note: This flag is required for proper typeface rendering
                setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
            }finally {
                attributesArray.recycle();
            }
        }
    }

    public void setFontType(Utils.FontType fontType) {
        this.mFontType = fontType;
        switch (mFontType){
            case LIGHT:
                setTypeface(getRobotoTypeface(mContext, LIGHT));
                return;
            case MEDIUM:
                setTypeface(getRobotoTypeface(mContext, MEDIUM));
                return;
            case REGULAR:
                setTypeface(getRobotoTypeface(mContext, REGULAR));
                return;
            case BOLD:
                setTypeface(getRobotoTypeface(mContext, BOLD));
                return;
            default:
                setTypeface(getRobotoTypeface(mContext, REGULAR));
        }
    }
}
