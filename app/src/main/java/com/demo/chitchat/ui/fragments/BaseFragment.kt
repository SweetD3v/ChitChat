package com.demo.chitchat.ui.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.demo.chitchat.R

abstract class BaseFragment<B : ViewBinding> : Fragment() {
    lateinit var ctx: Context
    abstract val binding: B
    private var askCount = 0

//    @get:LayoutRes
//    protected abstract val layoutRes: Int

    var hasInitializedRootView = false
    private var rootView: View? = null

    private val calendarPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results: Map<String, Boolean?> ->
            val granted = results.all { it.value == true }

            if (granted) {
                onPermissionGranted()
            } else {
                askCount++
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) && shouldShowRequestPermissionRationale(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
//                    askStoragePermission()
                } else {
                    showPermissionDialog()
                }
            }
        }

    private var permissionDialog: AlertDialog? = null

    private fun showPermissionDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(ctx, R.style.RoundedCornersDialog)
        builder.setTitle(R.string.permissions_required).setCancelable(false)
            .setMessage(R.string.you_need_to_give_some_required_permissions_to_run_this_app_smoothly)
            .setPositiveButton(R.string.settings) { dialog, _ ->
                dialog.dismiss()
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts(
                    "package", ctx.packageName, null
                )
                intent.data = uri
                startActivity(intent)
            }
        permissionDialog ?: let {
            permissionDialog = builder.create()
        }
        if (permissionDialog?.isShowing == false) {
            permissionDialog?.show()
        }
    }

//    private fun askStoragePermission() {
//        if (ctx.hasStoragePermission()) {
//            askCount = 2
//        } else {
//            if (askCount == 0) {
//                calendarPermissionLauncher.launch(
//                    arrayOf(
//                        Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR
//                    )
//                )
//            } else if (askCount == 2)
//                showPermissionDialog()
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBackBtnPressed()
                }
            })
    }

    override fun onResume() {
        super.onResume()

//        askCalendarPermission()

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBackBtnPressed()
                }
            })
    }

    abstract fun onBackBtnPressed()

    abstract fun onPermissionGranted()
}