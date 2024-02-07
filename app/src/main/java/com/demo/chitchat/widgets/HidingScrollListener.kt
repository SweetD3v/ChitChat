package com.demo.chitchat.widgets

import androidx.recyclerview.widget.RecyclerView


abstract class HidingScrollListener : RecyclerView.OnScrollListener() {
    private var scrolledDistance = 0
    private var controlsVisible = true

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
            onHide()
            controlsVisible = false
            scrolledDistance = 0
        } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
            onShow()
            controlsVisible = true
            scrolledDistance = 0
        }
        if (controlsVisible && dy > 0 || !controlsVisible && dy < 0) {
            scrolledDistance += dy
        }
    }

    /*@Override
    public void onScrollChange(View view, int dx, int dy, int i2, int i3) {
        if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
            onHide();
            controlsVisible = false;
            scrolledDistance = 0;
        } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
            onShow();
            controlsVisible = true;
            scrolledDistance = 0;
        }

        if ((controlsVisible && dy > 0) || (!controlsVisible && dy < 0)) {
            scrolledDistance += dy;
        }
    }*/
    abstract fun onHide()
    abstract fun onShow()

    companion object {
        private const val HIDE_THRESHOLD = 20
    }
}