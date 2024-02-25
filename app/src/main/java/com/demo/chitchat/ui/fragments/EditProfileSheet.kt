package com.demo.chitchat.ui.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import com.demo.chitchat.databinding.BottomSheetEditProfileBinding
import com.demo.chitchat.utils.setOnSingleClickListener
import com.demo.chitchat.utils.toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class EditProfileSheet : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetEditProfileBinding
    private var db: FirebaseFirestore? = null
    private var docRef: DocumentReference? = null
    private var edit: Boolean = false
    private lateinit var ctx: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!::binding.isInitialized) {
            binding = BottomSheetEditProfileBinding.inflate(inflater)
        }
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener {
            val bottomSheet =
                bottomSheetDialog.findViewById<FrameLayout>(
                    com.google.android.material.R.id.design_bottom_sheet
                )
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.skipCollapsed = true
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        return bottomSheetDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        edit = arguments?.getBoolean("edit") ?: false

        binding.run {
            btnUpdate.setOnSingleClickListener {
                val name = this.edtName.text.toString()
                val about = this.edtAbout.text.toString()
                val phoneNo = this.edtPhone.text.toString()
//                val phoneNo = FirebaseAuth.getInstance().currentUser?.phoneNumber

                if (edit) {
                    db?.runTransaction { transaction ->
                        docRef?.let { it1 ->
                            transaction.update(it1, "name", name)
                            transaction.update(it1, "about", about)
                            transaction.update(it1, "phone", phoneNo)
//                            transaction.update(it1, "url", "")
                        }
                    }?.addOnSuccessListener {
                        dismissAllowingStateLoss()
                    }
                } else {
                    val profileMap = hashMapOf<String, String>()
                    profileMap["name"] = name
                    profileMap["about"] = about
                    profileMap["phone"] = phoneNo
                    profileMap["url"] = ""
                    FirebaseAuth.getInstance().currentUser?.let {
                        profileMap["uid"] = it.uid
                    }

                    docRef?.set(profileMap)?.addOnSuccessListener {
                        ctx.toast("Saved")
                        dismissAllowingStateLoss()
                    }?.addOnFailureListener {
                        ctx.toast("Failed to save")
                        dismissAllowingStateLoss()
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "DetailsFragment"

        fun newInstance(db: FirebaseFirestore, docRef: DocumentReference?): EditProfileSheet {
            val fragment = EditProfileSheet()
            fragment.db = db
            fragment.docRef = docRef
            return fragment
        }
    }
}