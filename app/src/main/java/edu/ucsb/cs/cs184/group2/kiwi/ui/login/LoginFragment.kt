package edu.ucsb.cs.cs184.group2.kiwi.ui.login

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import edu.ucsb.cs.cs184.group2.kiwi.R
import edu.ucsb.cs.cs184.group2.kiwi.databinding.FragmentLoginBinding
import edu.ucsb.cs.cs184.group2.kiwi.ui.AccountViewModel


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val accountViewModel: AccountViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val loginViewModel =
            ViewModelProvider(this).get(LoginViewModel::class.java)

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        val mGoogleSignInClient : GoogleSignInClient = GoogleSignIn.getClient(activity as Activity, gso);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account : GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(context as Context)
        updateUI(account)

        val textView: TextView = binding.textLogin
        loginViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val signInButton : SignInButton = binding.signInButton
        signInButton.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, 1)
        }

        val signOutButton : Button = binding.signOutButton
        signOutButton.setOnClickListener {
            mGoogleSignInClient.signOut()
            updateUI(null)
            Log.i("LoginFragment", "Signing out...")
        }

        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 1) {
            // The Task returned from;this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account : GoogleSignInAccount = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            updateUI(account)

            requireView().findNavController().navigate(R.id.action_navigation_login_to_navigation_home)
            updateFirebaseUserProfile(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult: failed code " + e.statusCode)
            updateUI(null)
        }
    }

    private fun updateUI(account : GoogleSignInAccount? ) {
        if (account != null) {
            val loginViewModel =
                ViewModelProvider(this).get(LoginViewModel::class.java)

            // Update text on Login Page
            val text : String = "Welcome, " + account.displayName!! + "."
            loginViewModel.updateText(text)
            val textView: TextView = binding.textLogin
            textView.text = text

            // Update what account is logged in
            accountViewModel.updateAccount(account)

            Log.i("LoginFragment", account.displayName!! + " is logged in.")
        } else {
            val loginViewModel =
                ViewModelProvider(this).get(LoginViewModel::class.java)

            // Update text on Login Page
            val text : String = "This is the login page."
            loginViewModel.updateText(text)
            val textView: TextView = binding.textLogin
            textView.text = text

            // Update what account is logged in
            accountViewModel.updateAccount(null)

            Log.i("LoginFragment", "No one is logged in.")
        }

    }

    private fun updateFirebaseUserProfile(account : GoogleSignInAccount?) {
        if (account != null) {
            val database = Firebase.database
            val usersRef: DatabaseReference = database.getReference("users")
            val userId: String = account.id!!

            /*
            This is to check if the user already exists but we might not need this later.
             */
//            val userExistsQuery = usersRef.orderByKey().startAt(userId)
//            userExistsQuery.addListenerForSingleValueEvent(object: ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    for (user in snapshot.children) {
//                        Log.d("FirebaseLog", user.key + " -> " + user.value)
//                    }
//                }
//                override fun onCancelled(error: DatabaseError) {
//                    // Failed to read value
//                    Log.w("FirebaseLog", "Failed to read value.", error.toException())
//                }
//            })

            val keyedUserReference: DatabaseReference = usersRef.child(userId)
            val values: MutableMap<String, Any> = HashMap()
            values["name"] = account.displayName!!

            keyedUserReference.updateChildren(values)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}