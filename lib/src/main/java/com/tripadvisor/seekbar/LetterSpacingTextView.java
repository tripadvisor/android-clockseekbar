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
import android.text.SpannableString;
import android.text.style.ScaleXSpan;
import android.util.AttributeSet;

import static android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE;

/**
 * Text view that allows changing the letter spacing of the text.
 * Source: http://stackoverflow.com/questions/1640659/how-to-adjust-text-kerning-in-android-textview/16429758#16429758
 *
 * @author Pedro Barros (pedrobarros.dev at gmail.com)
 * @since May 7, 2013
 */
public class LetterSpacingTextView extends RobotoTextView {

    private float letterSpacing = 0.0F;
    private CharSequence originalText = "";


    public LetterSpacingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public float getLetterSpacing() {
        return letterSpacing;
    }

    public void setLetterSpacing(float mLetterSpacing) {
        this.letterSpacing = mLetterSpacing;
        requestLayout();
        invalidate();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        // Below if block taken from android source code, this ensures we dont have NPE in future.
        // However note that this was not causing FLT-1507
        if (text == null) {
            text = "";
        }
        originalText = text;
        applyLetterSpacing();
    }

    @Override
    public CharSequence getText() {
        return originalText;
    }

    private void applyLetterSpacing() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < originalText.length(); i++) {
            builder.append(originalText.charAt(i));
            if (i + 1 < originalText.length()) {
                builder.append('\u00A0');
            }
        }
        SpannableString finalText = new SpannableString(builder.toString());
        if (builder.toString().length() > 1) {
            for (int i = 1; i < builder.toString().length(); i += 2) {
                finalText.setSpan(new ScaleXSpan((letterSpacing + 1.0F) / 10.0F), i, i + 1, SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        super.setText(finalText, BufferType.SPANNABLE);
    }
}
