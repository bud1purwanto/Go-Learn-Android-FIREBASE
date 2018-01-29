package com.budi.go_learn.RecyclerView;

import android.view.View;

/**
 * Created by root on 11/1/17.
 */

public interface ClickListener {
    void onClick(View view, int position);
    void onLongClick(View view, int position);
}
