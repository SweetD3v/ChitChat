package com.demo.chitchat.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.chitchat.databinding.FragmentChatsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatsFragment : BaseFragment<FragmentChatsBinding>() {
    override val binding: FragmentChatsBinding by lazy { FragmentChatsBinding.inflate(layoutInflater) }
    var docRef: DocumentReference? = null
    val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    var currentUserId = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val user = FirebaseAuth.getInstance().currentUser
        currentUserId = user?.uid ?: ""
        docRef = db.collection("user").document(currentUserId)

        setupUsers()
    }

    private fun setupUsers() {
        binding.run {
            rvChats.apply {
                layoutManager = LinearLayoutManager(ctx)
                setHasFixedSize(true)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val query : Query = db.collection("user")
        val options : FirestoreRecyclerOptions
    }

    override fun onBackBtnPressed() {
    }

    override fun onPermissionGranted() {
    }

    companion object {
        fun newInstance(): ChatsFragment {
            val fragment = ChatsFragment()
            return fragment
        }
    }
}
