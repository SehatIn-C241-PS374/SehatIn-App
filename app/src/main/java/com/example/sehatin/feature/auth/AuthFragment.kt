package com.example.sehatin.feature.auth

import android.app.AlertDialog
import android.util.Log
import androidx.fragment.app.viewModels
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetCustomCredentialOption
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.sehatin.BuildConfig
import com.example.sehatin.R
import com.example.sehatin.application.base.BaseFragment
import com.example.sehatin.databinding.FragmentAuthBinding
import com.example.sehatin.utils.Toaster
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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

    private lateinit var auth: FirebaseAuth
    private val user : FirebaseUser? = null
    private val db : FirebaseFirestore = Firebase.firestore
    private val userRef = db.collection("users")

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentAuthBinding {
        return FragmentAuthBinding.inflate(inflater, container, false)
    }

    override fun setUpViews() {
        auth = Firebase.auth
        binding.apply {
            btnAuth.setOnClickListener {
                authProcess()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            backPressedCallback
        )

    }

    private fun authProcess() {
        val credentialsManager = CredentialManager.create(requireContext())

        val googleId = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.WEB_CLIENT_ID)
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
                    val user = Firebase.auth.currentUser
                    user?.let {
                        val userData = hashMapOf(
                            "uId" to user.uid,
                            "email" to user.email,
                            "username" to user.displayName
                        )

                        userRef.document(user.uid).set(userData)
                            .addOnSuccessListener {
                                Toaster.show(requireContext(), "Success")
                                findNavController().navigate(R.id.home)
                            }
                    }

                } else {
                    Toaster.show(requireContext(), "Failed to login, try again")
                }
            }

    }

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val alertDialog = AlertDialog.Builder(requireContext())
                .setTitle(R.string.dialog_title)
                .setMessage(R.string.dialog_message)
                .setPositiveButton(R.string.dialog_positive_button) { dialog, _ ->
                    dialog.dismiss()
                    requireActivity().finish()
                }
                .setNegativeButton(R.string.dialog_negative_button, null)
                .create()
            alertDialog.show()
        }
    }

}