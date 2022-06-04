package com.example.meditation.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.meditation.R
import com.example.meditation.databinding.FragmentSignInBinding
import com.example.meditation.theme.Theme
import com.example.meditation.util.GoogleSignInOption
import com.example.meditation.viewmodel.FirebaseAuthViewModel
import com.facebook.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.*


class SignInFragment : Fragment(), LifecycleOwner {

    private lateinit var binding: FragmentSignInBinding
    private lateinit var firebaseAuthViewModel: FirebaseAuthViewModel
    private lateinit var gsc : GoogleSignInClient
    private lateinit var callbackManager : CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FacebookSdk.sdkInitialize(requireContext());

        firebaseAuthViewModel = ViewModelProvider(this)[FirebaseAuthViewModel::class.java]
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        firebaseAuthViewModel.isFacebookLogin.observe(viewLifecycleOwner, Observer { isLogin ->
            if (isLogin){
                LoginManager.getInstance().logOut()
                firebaseAuthViewModel.isFacebookLogin.postValue(false)
            }
        })

        firebaseAuthViewModel.userMutableLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_signInFragment_to_homeFragment)
            }
        })

        firebaseAuthViewModel.isEmailAlreadyExists.observe(viewLifecycleOwner, Observer { isExist ->
            if (isExist == false){
                Toast.makeText(context, "Email này không tồn tại", Toast.LENGTH_SHORT).show()
                binding.btnSignIn.text = getText(R.string.sign_in)
                binding.progressSignIn.visibility = View.INVISIBLE
            }
        })

        binding = FragmentSignInBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        Theme.changeColorStatusBar(requireActivity().window, R.color.white, context)

        view.setOnClickListener {
            Theme.hideSoftKeyBoard(it, requireContext())
            binding.edtEmail.clearFocus()
            binding.edtPassword.clearFocus()
        }

        binding.tvForgotPassword.setOnClickListener { findNavController().navigate(R.id.action_signInFragment_to_forgotPasswordFragment) }

        binding.textViewLogin.setOnClickListener { findNavController().navigate(R.id.action_signInFragment_to_signUpFragment) }

        binding.btnSignIn.setOnClickListener {
            checkInfo()
        }

        binding.imgBack.setOnClickListener { findNavController().popBackStack() }

        gsc = GoogleSignIn.getClient(
            requireContext(),
            GoogleSignInOption.init(requireContext())
        )

        binding.btnFacebook.setOnClickListener { initFacebookLogin() }

        binding.btnGoogle.setOnClickListener { signInWithGoogle() }

        return view
    }

    private fun initFacebookLogin() {
        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().logInWithReadPermissions(this,
            listOf("email", "public_profile"))
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d("FbAuth", "facebook:onSuccess:$loginResult")
                firebaseAuthViewModel.firebaseAuthWithFaceBook(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d("FbAuth", "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d("FbAuth", "facebook:onError", error)
            }
        })

    }

//    private fun handleFacebookAccessToken(token: AccessToken) {
//        Log.d("FbAuth", "handleFacebookAccessToken:$token")
//
//        val credential = FacebookAuthProvider.getCredential(token.token)
//        mAuth.signInWithCredential(credential)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d("FbAuth", "signInWithCredential:success")
//                    val user = mAuth.currentUser
//                    updateUI(user)
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.d("FbAuth", "signInWithCredential:failure", task.exception)
//                    Toast.makeText(requireContext(), "Authentication failed.",
//                        Toast.LENGTH_SHORT).show()
//                }
//            }
//    }
//
//    private fun updateUI(user: FirebaseUser?) {
//        if (user != null){
//            Toast.makeText(requireContext(), "Sign in complete: $user", Toast.LENGTH_SHORT).show()
//        }else{
//            Toast.makeText(requireContext(), "Please sign in to continue.", Toast.LENGTH_SHORT).show()
//        }
//    }


    private fun signInWithGoogle() {
        gsc.signOut()
        val intent = gsc.signInIntent
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 64206){
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }

        if (requestCode == 100){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception

            if (task.isSuccessful){
                try {
                    val account = task.getResult(ApiException::class.java)
                    Log.d("googleSignIn", "firebaseAuthWithGoogle" + account.id)
                    firebaseAuthViewModel.firebaseAuthWithGoogle(account.idToken!!)
                } catch (e : ApiException){
                    Log.d("googleSignIn", "Google sign in failed", e)
                }
            }else{
                Log.d("googleSignIn", exception.toString())
            }
        }
    }

    private fun checkInfo() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        when{
            TextUtils.isEmpty(email) -> {
                binding.edtEmail.error = "Email không được để trống"
                binding.edtEmail.requestFocus()
            }
            !isEmailValid(email) -> {
                binding.edtEmail.error = "Email sai định dạng"
                binding.edtEmail.requestFocus()
            }
            TextUtils.isEmpty(password) -> {
                binding.edtPassword.error = "Password không được để trống"
                binding.edtPassword.requestFocus()
            }

            else -> {
                binding.btnSignIn.text = null
                binding.progressSignIn.visibility = View.VISIBLE
                firebaseAuthViewModel.login(email, password)
            }

        }
    }

    private fun isEmailValid(email : CharSequence) : Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}