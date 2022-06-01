package com.example.meditation.util

import android.content.Context
import com.example.meditation.R
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class GoogleSignInOption {
    companion object {
        fun init(context: Context): GoogleSignInOptions {
            return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.web_client_id))
                .requestEmail()
                .build()
        }
    }
}