package com.demo.chitchat

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import com.demo.chitchat.models.CallbackStatus
import com.demo.chitchat.models.LoginResult
import com.demo.chitchat.utils.TAG
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class ChatViewModel(val app: Application) : AndroidViewModel(app) {

    var mAuth: FirebaseAuth? = null

    fun loginWithPhoneNo(
        activity: AppCompatActivity,
        cCode: String?,
        phoneNo: String?,
        callbackStatus: (LoginResult) -> Unit
    ) {
        mAuth = FirebaseAuth.getInstance()

        if (phoneNo?.isEmpty() == true || cCode?.isEmpty() == true) {
            Toast.makeText(
                app.applicationContext,
                "Please fill all required fields",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signIn(activity, credential, callbackStatus)
            }

            override fun onVerificationFailed(exception: FirebaseException) {
                callbackStatus(LoginResult(CallbackStatus.FAILED, ""))
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.e(TAG, "onCodeSent:$verificationId")
                callbackStatus(LoginResult(CallbackStatus.OTPSENT, verificationId))

                // Save verification ID and resending token so we can use them later

            }
        }

        val options = PhoneAuthOptions.newBuilder(mAuth!!)
            .setPhoneNumber(cCode.toString() + phoneNo.toString()) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun signIn(
        activity: AppCompatActivity,
        credential: PhoneAuthCredential,
        callbackStatus: (LoginResult) -> Unit
    ) {
        mAuth?.signInWithCredential(credential)
            ?.addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user
                    callbackStatus(LoginResult(CallbackStatus.SUCCESS, ""))
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        callbackStatus(
                            LoginResult(
                                CallbackStatus.FAILED,
                                error = "The verification code you entered is invalid"
                            )
                        )
                    } else {
                        callbackStatus(
                            LoginResult(
                                CallbackStatus.FAILED,
                                error = task.exception?.localizedMessage
                            )
                        )
                    }
                    // Update UI
                }
            }
    }

    fun getCurrentUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }
}