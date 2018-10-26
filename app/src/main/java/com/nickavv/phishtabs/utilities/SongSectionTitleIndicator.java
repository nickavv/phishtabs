package com.nickavv.phishtabs.utilities;

import android.content.Context;
import android.util.AttributeSet;

import com.nickavv.phishtabs.R;
import com.nickavv.phishtabs.objects.Song;

import xyz.danoz.recyclerviewfastscroller.sectionindicator.title.SectionTitleIndicator;

/**
 * Created by Nick on 11/13/2015.
 */
public class SongSectionTitleIndicator extends SectionTitleIndicator<Character> {


    public SongSectionTitleIndicator(Context context) {
        super(context);
    }

    public SongSectionTitleIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SongSectionTitleIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setSection(Character object) {
        setTitleText(object.charValue() + "");
    }
}
