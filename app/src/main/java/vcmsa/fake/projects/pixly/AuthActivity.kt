package vcmsa.fake.projects.pixly

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import vcmsa.fake.projects.pixly.R.id.googleSignInBtn
import java.util.*

class AuthActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var googleSignInClient: GoogleSignInClient
    val clientId = getString(R.string.default_web_client_id)

    private lateinit var profileImageView: ImageView
    private val RC_GOOGLE_SIGN_IN = 1002
    private var selectedImageUri: Uri? = null

    private val IMAGE_PICK_CODE = 1001

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        // Google Sign-In setup
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // <<-- Important
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val googleSignInBtn = findViewById<com.google.android.gms.common.SignInButton>(
            googleSignInBtn
        )
        googleSignInBtn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
        }


        val nameEditText = findViewById<EditText>(R.id.nameInput)
        val emailEditText = findViewById<EditText>(R.id.emailInput)
        val passwordEditText = findViewById<EditText>(R.id.passwordInput)
        val signUpBtn = findViewById<Button>(R.id.signUpBtn)
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        profileImageView = findViewById(R.id.profileImageView)

        // Pick profile picture
        profileImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        signUpBtn.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || selectedImageUri == null) {
                Toast.makeText(this, "Please fill all fields and pick an image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener
                uploadProfileImageToStorage(uid) { imageUrl ->
                    val userMap = hashMapOf(
                        "uid" to uid,
                        "email" to email,
                        "name" to name,
                        "profilePicUrl" to imageUrl
                    )
                    db.collection("users").document(uid).set(userMap).addOnSuccessListener {
                        Toast.makeText(this, "Registered successfully!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Registration failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        loginBtn.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Login failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadProfileImageToStorage(uid: String, onComplete: (String) -> Unit) {
        val filename = "profilePics/$uid-${UUID.randomUUID()}.jpg"
        val ref = storage.reference.child(filename)
        selectedImageUri?.let {
            ref.putFile(it).addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    onComplete(uri.toString())
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Handle profile image selection
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            findViewById<ImageView>(R.id.profileImageView).setImageURI(selectedImageUri)
        }

        // Handle Google Sign-In result
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnSuccessListener {
            val user = auth.currentUser ?: return@addOnSuccessListener
            val userDocRef = db.collection("users").document(user.uid)

            // Store user if not already in Firestore
            userDocRef.get().addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    val userMap = hashMapOf(
                        "uid" to user.uid,
                        "email" to user.email,
                        "name" to user.displayName,
                        "profilePicUrl" to user.photoUrl.toString()
                    )
                    userDocRef.set(userMap)
                }
            }

            Toast.makeText(this, "Google Sign-In successful!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Firebase auth failed: ${it.message}", Toast.LENGTH_SHORT).show()
        }

    }
}
