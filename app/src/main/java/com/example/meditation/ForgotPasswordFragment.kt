package com.example.meditation

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.meditation.databinding.FragmentForgotPasswordBinding
import com.example.meditation.theme.Theme
import com.example.meditation.viewmodel.FirebaseAuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class ForgotPasswordFragment : Fragment(), LifecycleOwner {

    private lateinit var binding : FragmentForgotPasswordBinding
    private lateinit var firebaseAuthViewModel: FirebaseAuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuthViewModel = ViewModelProvider(this)[FirebaseAuthViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgotPasswordBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        binding.imgBack.setOnClickListener { findNavController().popBackStack() }

        firebaseAuthViewModel.isEmailAlreadyExists.observe(viewLifecycleOwner, Observer { isExist ->
            if (!isExist){
                Toast.makeText(context, "Email này không tồn tại", Toast.LENGTH_SHORT).show()
                binding.btnContinue.text = getString(R.string.Continue)
                binding.progressContinue.visibility = View.INVISIBLE
            }
        })

        firebaseAuthViewModel.isEmailSent.observe(viewLifecycleOwner, Observer { isSent ->
            if (isSent){
                Toast.makeText(requireContext(), "Một email khôi phục mật khẩu đã được gửi tới địa chỉ email của bạn.", Toast.LENGTH_SHORT).show()
                binding.btnContinue.text = getString(R.string.Continue)
                binding.progressContinue.visibility = View.INVISIBLE
            }
        })

        view.setOnClickListener {
            Theme.hideSoftKeyBoard(it, requireContext())
            binding.edtEmail.clearFocus()
        }

        binding.btnContinue.setOnClickListener { checkInfo() }

        return view
    }

    private fun checkInfo() {
        val email = binding.edtEmail.text.toString().trim()

        when{
            TextUtils.isEmpty(email) -> {
                binding.edtEmail.error = "Email không được để trống"
                binding.edtEmail.requestFocus()
            }
            !isEmailValid(email) -> {
                binding.edtEmail.error = "Email sai định dạng"
                binding.edtEmail.requestFocus()
            }
            else -> {
                binding.btnContinue.text = null
                binding.progressContinue.visibility = View.VISIBLE

                GlobalScope.launch(Dispatchers.Main) {
                    firebaseAuthViewModel.sendPasswordResetEmail(email)
                    delay(1000)
                    binding.btnContinue.text = getString(R.string.Continue)
                    binding.progressContinue.visibility = View.INVISIBLE
                }

            }

        }
    }

    private fun isEmailValid(email : CharSequence) : Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}