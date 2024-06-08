package com.example.sehatin.feature.auth

import android.credentials.CredentialManager
import android.credentials.GetCredentialResponse
import android.util.Log
//import androidx.credentials.CredentialManager
import androidx.fragment.app.viewModels
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
//import androidx.credentials.CustomCredential
//import androidx.credentials.GetCredentialRequest
//import androidx.credentials.GetCredentialResponse
//import androidx.credentials.GetCustomCredentialOption
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.sehatin.R
import com.example.sehatin.application.base.BaseFragment
import com.example.sehatin.databinding.FragmentAuthBinding


  import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.example.sehatin.utils.Toaster
//import com.google.android.libraries.identity.googleid.GetGoogleIdOption
//import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
//import com.google.firebase.Firebase
//import com.google.firebase.auth.AuthCredential
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.GoogleAuthProvider
//import com.google.firebase.auth.auth
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.flags.HiltWrapper_FragmentGetContextFix_FragmentGetContextFixModule
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthFragment : BaseFragment<FragmentAuthBinding>() {

    private val viewModel: AuthViewModel by viewModels()
//    private lateinit var auth: FirebaseAuth

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentAuthBinding {
        return FragmentAuthBinding.inflate(inflater, container, false)
    }

    override fun setupObserver() {
        super.setupObserver()
    }

    override fun setUpViews() {
//        auth = Firebase.auth
        binding.apply {
            btnAuth.setOnClickListener {
//                authProcess()
            }
        }

    }

    private fun authProcess() {
        val credentialsManager = CredentialManager.create(requireContext())

        val googleId = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.web_client_id))
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleId)
            .build()

        lifecycleScope.launch {
            try {
                val result : GetCredentialResponse = credentialsManager.getCredential(
                    requireContext(),
                    request = request
                )
                handleSignIn(result)
            } catch (e: Exception) {
                Log.d("Error", e.message.toString())

            }

        }

    }

    private fun handleSignIn(result: GetCredentialResponse) {
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCrednetial = GoogleIdTokenCredential.createFrom(credential.data)
                        firebaseAuthWithGoogle(googleIdTokenCrednetial.idToken)
                    } catch (e: Exception) {
                        Log.e("Debug", "Received an invalid google id token response", e)
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    Log.e("Debug", "Unexpected type of credential")
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(googleIdTokenCredential: String) {
        val credentials : AuthCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential, null)
        auth.signInWithCredential(credentials)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    findNavController().navigate(R.id.home)
                } else {
//                    Toaster.show(requireContext(), "Failed to login, try again")
                }
            }
    }

}