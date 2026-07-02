package com.example.myroamnepal.repo

import com.example.myroamnepal.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserRepoImpl : UserRepo {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val userRef = database.getReference("users")

    override fun sendPasswordResetEmail(email: String, callback: (Boolean, String) -> Unit) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Reset link sent to your email.")
            } else {
                callback(false, it.exception?.message ?: "Failed to send reset email.")
            }
        }
    }

    override fun logOut(callback: (Boolean, String) -> Unit) {
        auth.signOut()
        callback(true, "Logged out successfully.")
    }

    override fun getCurrentUser(callback: (Boolean, UserModel?) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            userRef.child(currentUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(UserModel::class.java)
                    callback(true, user)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, null)
                }
            })
        } else {
            callback(false, null)
        }
    }

    override fun login(email: String, password: String, callback: (Boolean, String, UserModel?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = task.result?.user?.uid ?: ""
                userRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(UserModel::class.java)
                        callback(true, "Login Successful", user)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        callback(false, "Failed to fetch user data", null)
                    }
                })
            } else {
                callback(false, task.exception?.message ?: "Login Failed", null)
            }
        }
    }

    override fun changePassword(oldPassword: String, newPassword: String, callback: (Boolean, String) -> Unit) {
        val user = auth.currentUser
        if (user != null && user.email != null) {
            auth.signInWithEmailAndPassword(user.email!!, oldPassword).addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            callback(true, "Password changed successfully.")
                        } else {
                            callback(false, updateTask.exception?.message ?: "Failed to update password.")
                        }
                    }
                } else {
                    callback(false, "Incorrect old password.")
                }
            }
        } else {
            callback(false, "User not authenticated.")
        }
    }

    override fun updateUser(userModel: UserModel, callback: (Boolean, String) -> Unit) {
        userRef.child(userModel.uid).updateChildren(userModel.toMap()).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Profile updated successfully.")
            } else {
                callback(false, it.exception?.message ?: "Update failed.")
            }
        }
    }

    override fun addUser(id: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        userRef.child(id).setValue(model).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "User data saved.")
            } else {
                callback(false, it.exception?.message ?: "Failed to save user data.")
            }
        }
    }

    override fun rollbackCurrentUserRegistration() {
        auth.currentUser?.delete()
    }

    override fun register(email: String, password: String, callback: (Boolean, String, String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Registration Successful", it.result?.user?.uid ?: "")
            } else {
                callback(false, it.exception?.message ?: "Registration Failed", "")
            }
        }
    }

    override fun getAllUsers(callback: (Boolean, String, List<UserModel>) -> Unit) {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = mutableListOf<UserModel>()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(UserModel::class.java)
                    if (user != null) users.add(user)
                }
                callback(true, "Success", users)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, emptyList())
            }
        })
    }

    override fun deleteUser(uid: String, callback: (Boolean, String) -> Unit) {
        userRef.child(uid).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "User deleted.")
            } else {
                callback(false, it.exception?.message ?: "Failed to delete user.")
            }
        }
    }
}