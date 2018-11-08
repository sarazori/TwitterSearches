package com.zoris.twittersearches;

import android.support.annotation.NonNull;

import java.util.Calendar;

public class Query {

    private String tag;
    private Calendar time;

    public Query(String tag, Calendar time) {
        this.tag = tag;
        this.time = time;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object q){
        if (q instanceof Query) {
            return this.tag.equals(((Query) q).getTag());
        }
        return false;
    }
}
