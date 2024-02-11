package com.demo.chitchat.widgets

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.demo.chitchat.R
import com.demo.chitchat.utils.dpToPx

class ProgressDialogTrans(val context: Context) {
    private var dialog: Dialog? = null

    fun showDialog(text: String, cancelable: Boolean) {
        dialog = Dialog(context, R.style.DialogThemeTransp)
        dialog?.setCancelable(false)
        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.VERTICAL
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        linearLayout.layoutParams = layoutParams
        val progressBar = ProgressBar(context)
        progressBar.indeterminateDrawable.setTint(context.resources.getColor(R.color.color_primary))
        val layoutParams_progress = LinearLayout.LayoutParams(dpToPx(48), dpToPx(48))
        layoutParams_progress.gravity = Gravity.CENTER
        progressBar.layoutParams = layoutParams_progress
        linearLayout.addView(progressBar)
        val textView = TextView(context)
        textView.textSize = 18f
        textView.text = text
        textView.setTextColor(Color.WHITE)
        textView.gravity = Gravity.CENTER_VERTICAL
        textView.setPadding(0, dpToPx(16), 0, 0)
        val linearlayoutParams_text = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textView.layoutParams = linearlayoutParams_text
        linearLayout.addView(textView)
        dialog?.window?.setContentView(linearLayout, layoutParams)
        dialog?.setCancelable(cancelable)
        if (dialog != null && dialog?.isShowing == false) {
            dialog?.show()
        }
    }

    fun dismissDialog() {
        if (dialog?.isShowing == true) {
            dialog?.dismiss()
        }
    }

    fun setOnDismissListener(callback: () -> Unit) {
        dialog?.setOnDismissListener { callback() }
    }
}