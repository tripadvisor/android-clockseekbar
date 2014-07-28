/*
 * Copyright (C) 2014 TripAdvisor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author ksarmalkar 7/28/2014
 */

package com.tripadvisor.seekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.tripadvisor.seekbar.util.Utils;

import static com.tripadvisor.seekbar.util.Utils.FontType.BOLD;
import static com.tripadvisor.seekbar.util.Utils.FontType.LIGHT;
import static com.tripadvisor.seekbar.util.Utils.FontType.MEDIUM;
import static com.tripadvisor.seekbar.util.Utils.FontType.REGULAR;
import static com.tripadvisor.seekbar.util.Utils.FontType.values;
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

        if (!isInEditMode()) {
            TypedArray attributesArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RobotoTextView, 0, 0);
            try {
                mFontType = values()[attributesArray.getInteger(R.styleable.RobotoTextView_fontType, 0)];
                setFontType(mFontType);
                // Note: This flag is required for proper typeface rendering
                setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
            } finally {
                attributesArray.recycle();
            }
        }
    }

    public void setFontType(Utils.FontType fontType) {
        this.mFontType = fontType;
        switch (mFontType) {
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
