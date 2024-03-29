package com.example.meditation.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.meditation.R
import com.example.meditation.databinding.FragmentSignUpBinding
import com.example.meditation.theme.Theme
import com.example.meditation.util.GoogleSignInOption
import com.example.meditation.viewmodel.FirebaseAuthViewModel
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough

class SignUpFragment : Fragment(), LifecycleOwner {

    private lateinit var binding : FragmentSignUpBinding
    private lateinit var firebaseAuthViewModel: FirebaseAuthViewModel
    private lateinit var gsc : GoogleSignInClient
    private lateinit var callbackManager : CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough().apply {
            duration = 300L
        }

        returnTransition = MaterialElevationScale(true).apply {
            duration = 50L
        }

        FacebookSdk.sdkInitialize(requireContext());

        firebaseAuthViewModel = ViewModelProvider(this)[FirebaseAuthViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firebaseAuthViewModel.isFacebookLogin.observe(viewLifecycleOwner, Observer { isLogin ->
            if (isLogin){
                LoginManager.getInstance().logOut()
                firebaseAuthViewModel.isFacebookLogin.postValue(false)
            }
        })

        firebaseAuthViewModel.userMutableLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show()
                materialMotion()
                findNavController().navigate(R.id.action_signUpFragment_to_homeFragment)
            }
        })

        firebaseAuthViewModel.isEmailAlreadyExists.observe(viewLifecycleOwner, Observer { isExist ->
            if (isExist){
                Toast.makeText(context, "Email đã tồn tại", Toast.LENGTH_SHORT).show()
                binding.btnSignUp.text = getText(R.string.Continue)
                binding.progressSignUp.visibility = View.INVISIBLE
            }
        })

        binding = FragmentSignUpBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        Theme.changeColorStatusBar(requireActivity().window, R.color.white, context)

        view.setOnClickListener {
            Theme.hideSoftKeyBoard(it, requireContext())
            binding.edtName.clearFocus()
            binding.edtEmail.clearFocus()
            binding.edtPassword.clearFocus()
        }

        binding.textViewLogin.setOnClickListener { findNavController().navigate(R.id.action_signUpFragment_to_signInFragment) }

        binding.btnSignUp.setOnClickListener { checkInfo() }

        binding.imgBack.setOnClickListener { findNavController().popBackStack() }

        gsc = GoogleSignIn.getClient(
            requireContext(),
            GoogleSignInOption.init(requireContext())
        )

        binding.btnFacebook.setOnClickListener { initFacebookLogin() }

        binding.btnGoogle.setOnClickListener { signInWithGoogle() }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    private fun initFacebookLogin() {
        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().logInWithReadPermissions(this,
            listOf("email", "public_profile"))
        LoginManager.getInstance().registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
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
        val name = binding.edtName.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        when {
            TextUtils.isEmpty(name) -> {
                binding.edtName.error = "Tên không được để trống"
                binding.edtName.requestFocus()
            }
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
            password.length in 0..5 -> {
                binding.edtPassword.error = "Password cần tối thiểu 6 ký tự"
                binding.edtPassword.requestFocus()
            }
            !binding.cbTerm.isChecked -> {
                Toast.makeText(context, "Chưa đồng ý điều khoản sử dụng", Toast.LENGTH_SHORT).show()
            }

//            else -> createUser(name, email, password)
            else -> {
                binding.btnSignUp.text = null
                binding.progressSignUp.visibility = View.VISIBLE
                firebaseAuthViewModel.register(name, email, password)
            }
        }
    }

    private fun isEmailValid(email : CharSequence) : Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun materialMotion(){
        exitTransition = MaterialFadeThrough().apply {
            duration = 100L
        }

        reenterTransition = MaterialFadeThrough().apply {
            duration = 300L
        }
    }

}