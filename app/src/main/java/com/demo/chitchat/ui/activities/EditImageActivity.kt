package com.demo.chitchat.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.demo.chitchat.databinding.ActivityEditImageBinding
import com.demo.chitchat.utils.gone
import com.demo.chitchat.utils.setOnSingleClickListener
import com.demo.chitchat.utils.toast
import com.demo.chitchat.utils.visible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class EditImageActivity : AppCompatActivity() {

    private val binding by lazy { ActivityEditImageBinding.inflate(layoutInflater) }

    private var firebaseStorage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    var imageUri: Uri? = null
    val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private var currentUid: String? = null
    var docRef: DocumentReference? = null
    private var url: String? = null

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { picUri ->
            picUri?.let { uri ->

                // Persist media permission for longer access (We are uploading image to the database, so we might need it)
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                contentResolver.takePersistableUriPermission(uri, flag)

                imageUri = uri
                Glide.with(this).load(imageUri).into(binding.imgProfile)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val user = FirebaseAuth.getInstance().currentUser
        storageReference = firebaseStorage?.getReference("profile images")
        currentUid = user?.uid

        binding.run {
            btnSave.setOnSingleClickListener {
                uploadProfilePic()
            }
        }
    }

    private fun getProfilePic() {
        currentUid?.let {
            docRef = db.collection("user").document(it)

            docRef?.get()
                ?.addOnCompleteListener { task ->
                    if (task.result.exists()) {
                        url = task.result.getString("url")

                        if (url == "") {
                            binding.btnDelete.gone()
                        } else {
                            binding.btnDelete.visible()
                            Glide.with(this).load(url).into(binding.imgProfile)
                        }
                    } else {
                        toast("Profile doesn't exist")
                    }
                }
        }
    }

    private fun uploadProfilePic() {
        binding.progressBar.visible()
        val reference = storageReference?.child(System.currentTimeMillis().toString() + ".jpg")
        imageUri?.let {
            val uploadTask = reference?.putFile(it)
            val urlTask = uploadTask?.continueWithTask { task ->
                if (!task.isSuccessful)
                    throw Exception(task.exception)
                reference.downloadUrl
            }?.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    val downloadUri = task.result

                    val sfDocRef = currentUid?.let { uid ->
                        db.collection("user").document(uid)
                    }

                    db.runTransaction { transaction ->
                        val snapshot = sfDocRef?.let { docRef ->
                            transaction.get(docRef)

                            transaction.update(sfDocRef, "url", downloadUri.toString())
                        }
                    }.addOnSuccessListener {
                        binding.progressBar.gone()

                        val map = hashMapOf<String, Any>()
                        map["url"] = downloadUri.toString()
                    }.addOnFailureListener {
                        toast("Failed to upload pic")
                    }
                }
            }
        } ?: toast("Select an image first.")
    }

    private fun deleteProfilePic() {
        url?.let {
            FirebaseStorage.getInstance().getReferenceFromUrl(it)
                .delete().addOnCompleteListener {
                    toast("Profile pic deleted")
                }

            currentUid?.let { uid ->
                val sfDocRef = db.collection("user").document(uid)
                db.runTransaction { transaction ->
                    val snapshot = transaction.get(sfDocRef)
                    transaction.update(sfDocRef, "url", "")
                }.addOnSuccessListener {
                    binding.progressBar.gone()

                    val map = hashMapOf<String, String>()
                    map["url"] = ""
                }
            }
        }
    }
}