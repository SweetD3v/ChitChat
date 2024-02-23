package com.demo.chitchat.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.ViewModelProvider
import com.demo.chitchat.ChatViewModel
import com.demo.chitchat.R
import com.demo.chitchat.databinding.ActivityLoginBinding
import com.demo.chitchat.models.CallbackStatus
import com.demo.chitchat.utils.adjustInsetsBothVisible
import com.demo.chitchat.utils.setOnSingleClickListener
import com.demo.chitchat.utils.toast
import com.demo.chitchat.widgets.ProgressDialogTrans
import com.google.firebase.auth.PhoneAuthProvider

class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    override val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val viewModel: ChatViewModel by lazy {
        ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(
                application
            )
        )[ChatViewModel::class.java]
    }

    private var verificationId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        inLogin = true
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {
            root.adjustInsetsBothVisible(this@LoginActivity, { top ->
                clMain.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    topMargin = top

                }
            }, { bottom ->

            })

            btnLogin.setOnSingleClickListener {
                val pd = ProgressDialogTrans(this@LoginActivity)
                pd.showDialog("Please wait...", false)

                viewModel.loginWithPhoneNo(
                    this@LoginActivity,
                    edtCC.text?.toString(),
                    edtPhoneNo.text?.toString()
                ) { loginResult ->
                    when (loginResult.status) {
                        CallbackStatus.FAILED -> {
                            pd.dismissDialog()
                        }

                        CallbackStatus.OTPSENT -> {
                            pd.dismissDialog()
                            verificationId = loginResult.verificationId
                            clMain.transitionToState(R.id.scene_otp)
                        }

                        CallbackStatus.SUCCESS -> {
                            pd.dismissDialog()
                            Intent(this@LoginActivity, MainActivity::class.java).apply {
                                this.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                startActivity(this)
                            }
                        }
                    }
                }
            }

            btnVerify.setOnSingleClickListener {
                val credential =
                    PhoneAuthProvider.getCredential(verificationId, edtPhoneNoOTP.text.toString())
                val pd = ProgressDialogTrans(this@LoginActivity)
                pd.showDialog("Please wait...", false)

                viewModel.signIn(this@LoginActivity, credential) { loginResult ->
                    when (loginResult.status) {
                        CallbackStatus.SUCCESS -> {
                            pd.dismissDialog()
                            toast("Login successful")
                            Intent(this@LoginActivity, MainActivity::class.java).apply {
                                this.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                startActivity(this)
                            }
                        }

                        CallbackStatus.FAILED -> {
                            pd.dismissDialog()
                            toast(loginResult.error)
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (viewModel.getCurrentUser() != null) {
            Intent(this@LoginActivity, MainActivity::class.java).apply {
                this.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(this)
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}