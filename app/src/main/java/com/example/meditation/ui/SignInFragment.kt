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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException

class SignInFragment : Fragment(), LifecycleOwner {

    private lateinit var binding: FragmentSignInBinding
    private lateinit var firebaseAuthViewModel: FirebaseAuthViewModel
    private lateinit var gsc : GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuthViewModel = ViewModelProvider(this)[FirebaseAuthViewModel::class.java]
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

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

//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.web_client_id))
//            .requestEmail()
//            .build()
//
        gsc = GoogleSignIn.getClient(
            requireContext(),
            GoogleSignInOption.init(requireContext())
        )

//        gsc = com.example.meditation.util.GoogleSignInOption.init(gsc, requireContext())

        binding.btnGoogle.setOnClickListener { signInWithGoogle() }

        return view
    }


    private fun signInWithGoogle() {
        gsc.signOut()
        val intent = gsc.signInIntent
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

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