package com.example.ttapp
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import com.example.ttapp.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivitySettingsBinding

    private lateinit var profileName: TextView
    private lateinit var profileUsername: TextView
    private lateinit var email: TextView
    private lateinit var password: TextView
    private lateinit var btnEdit: Button
    private lateinit var btnLogout: Button
    private lateinit var profileImg: CircleImageView
    private lateinit var changeProfileImg: ImageView

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var currentUser: FirebaseUser

    private var imageUri: Uri? = null
//    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == RESULT_OK) {
//            val data: Intent? = result.data
//            imageUri = data?.data
//        }
//    }
private val pickImageLauncher =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            imageUri = data?.data
            imageUri?.let { uploadImageToFirebase(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        profileName = findViewById(R.id.profileName)
        profileUsername = findViewById(R.id.profileUsername)
        email = findViewById(R.id.email)
        password = findViewById(R.id.profilePassword)
        btnEdit = findViewById(R.id.btnEdit)
        btnLogout = findViewById(R.id.btnLogout)
        profileImg = findViewById(R.id.profileImg)
        changeProfileImg = findViewById(R.id.changeProfileImg)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        currentUser = auth.currentUser!!

        fetchAndSetUserData()

        changeProfileImg.setOnClickListener{
            pickImageFromGallery()
//            imageUri?.let { it1 -> uploadImageToFirebase(it1) }
        }

        // Set click listeners
        btnEdit.setOnClickListener {
            showPasswordDialog()
        }

        btnLogout.setOnClickListener {
            auth.signOut()

            val intent = Intent(this, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun fetchAndSetUserData() {
        val uid = currentUser.uid
        val userReference = database.reference.child("users").child(uid)


        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val authHelper = snapshot.getValue(AuthHelper::class.java)
                authHelper?.let {
                    profileName.text = it.name
                    profileUsername.text = it.username
                    email.text = it.email
                    password.text = it.password
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("SettingsActivity", "Failed to read user data: ${error.message}")
            }
        })
    }

    @SuppressLint("IntentReset")
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            // Launch the intent
            pickImageLauncher.launch(intent)
        }
    }


    private fun uploadImageToFirebase(imageUri: Uri) {
        val uid = currentUser.uid
        val userReference = database.reference.child("users").child(uid)

        // Upload the image to Firebase Storage
        userReference.child("profileImage").setValue(imageUri.toString())
            .addOnSuccessListener {
                Log.d("&succes", imageUri.toString())
                Toast.makeText(this, "Profile image uploaded successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                // Handle the image upload failure
                Log.e("&SettingsActivity", "Failed to upload image: ${exception.message}")
                Toast.makeText(this, "Failed to upload profile image", Toast.LENGTH_SHORT).show()
            }
        // Create a reference to the user's profile image in Firebase Storage
//        val storageReference = userReference.child("profileImage.jpg")
//        Log.e("++++imageUri SettingsActivity ", imageUri.path.toString())
//
//
//        // Upload the image to Firebase Storage
//        storageReference.setValue(imageUri)
//            .addOnSuccessListener {
//                Log.e("++++imageUri SettingsActivity ", imageUri.path.toString())
//
//                // Image upload successful, get the download URL
////                storageReference.downloadUrl.addOnSuccessListener { downloadUri ->
////                    // Update the user's profile image URL in Firebase Realtime Database
////                    userReference.child("profileImageURL").setValue(downloadUri.toString())
////                }
//            }
//            .addOnFailureListener { exception ->
//                // Handle the image upload failure
//                Log.e("----imageUri SettingsActivity ", imageUri.path.toString())
//                Log.e("SettingsActivity", "Failed to upload image: ${exception.message}")
//            }
    }

    private fun showPasswordDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_password, null)
        builder.setView(dialogView)

        val passwordInput = dialogView.findViewById<EditText>(R.id.passwordInput)
        val alertDialog = builder.create()
        alertDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        builder.setPositiveButton("OK") { dialog, _ ->
            val enteredPassword = passwordInput.text.toString()
            if (enteredPassword == password.text.toString()) { // Replace currentUserPassword with the actual user's password
                dialog.dismiss()
                startEditProfileActivity()
            } else {
                Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun startEditProfileActivity() {
        val intent = Intent(this, EditProfileActivity::class.java)
        intent.putExtra("name", profileName.text.toString())
        intent.putExtra("username", profileUsername.text.toString())
        intent.putExtra("email", email.text.toString())
        intent.putExtra("password", password.text.toString())
        startActivity(intent)
    }
}
