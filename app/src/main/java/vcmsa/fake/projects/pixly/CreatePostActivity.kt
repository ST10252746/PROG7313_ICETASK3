package vcmsa.fake.projects.pixly

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class CreatePostActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var captionEditText: EditText
    private lateinit var selectedImageUri: Uri

    private val PICK_IMAGE_REQUEST = 1
    private val storageRef = FirebaseStorage.getInstance().reference
    private val firestore = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        imageView = findViewById(R.id.postImageView)
        captionEditText = findViewById(R.id.captionEditText)

        findViewById<Button>(R.id.selectImageBtn).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        findViewById<Button>(R.id.uploadBtn).setOnClickListener {
            if (::selectedImageUri.isInitialized) {
                uploadImageToFirebase()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data!!
            imageView.setImageURI(selectedImageUri)
        }
    }

    private fun uploadImageToFirebase() {
        val imageRef = storageRef.child("posts/${UUID.randomUUID()}.jpg")
        imageRef.putFile(selectedImageUri).addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                savePostToFirestore(uri.toString())
            }
        }
    }

    private fun savePostToFirestore(imageUrl: String) {
        val post = hashMapOf(
            "imageUrl" to imageUrl,
            "caption" to captionEditText.text.toString(),
            "userId" to user.uid,
            "username" to user.email,
            "timestamp" to FieldValue.serverTimestamp()
        )

        firestore.collection("posts").add(post).addOnSuccessListener {
            Toast.makeText(this, "Posted!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
