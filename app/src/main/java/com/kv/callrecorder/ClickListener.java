package com.kv.callrecorder;

public interface ClickListener {
    void onClick(int position, String s, String title, String date_time);

    void onSelect(boolean isChecked, int position);
}
