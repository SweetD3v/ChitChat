package com.demo.chitchat.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.demo.chitchat.databinding.ActivityProfileBinding
import com.demo.chitchat.utils.setOnSingleClickListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : BaseActivity<ActivityProfileBinding>() {
    override val binding: ActivityProfileBinding by lazy {
        ActivityProfileBinding.inflate(
            layoutInflater
        )
    }

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { picUri ->
            picUri?.let { uri ->

                // Persist media permission for longer access (We are uploading image to the database, so we might need it)
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                contentResolver.takePersistableUriPermission(uri, flag)


            }
        }

    private val db: FirebaseFirestore get() = FirebaseFirestore.getInstance()
    private var docRef: DocumentReference? = null
    private var currentUserId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val user = FirebaseAuth.getInstance().currentUser

        binding.run {
            user?.let {
                currentUserId = it.uid
                docRef = db.collection("user").document(currentUserId)
            }

            imgProfile.setOnSingleClickListener {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            btnAdd.setOnSingleClickListener {
                if (btnAdd.text.toString() == "Add")
                    showNameSheet()
                else if (btnAdd.text.toString() == "Edit")
                    showEditSheet()
            }

            btnLogout.setOnSingleClickListener {
                FirebaseAuth.getInstance().signOut()
                Intent(this@ProfileActivity, LoginActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(this)
                }
            }
        }
    }

    private fun showEditSheet() {

    }

    private fun showNameSheet() {

    }
}