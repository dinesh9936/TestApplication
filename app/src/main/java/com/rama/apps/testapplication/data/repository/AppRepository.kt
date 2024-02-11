package com.rama.apps.testapplication.data.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rama.apps.testapplication.data.model.Video
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class AppRepository @Inject constructor() {


    companion object {
        private const val TAG = "AppRepository"
    }


    fun getMovies(): Flow<List<Video>> = callbackFlow {
        Log.d(TAG, "getMovies: ")
        val databaseReference = FirebaseDatabase.getInstance().getReference("videos")

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val movies = mutableListOf<Video>()
                for (movieSnapshot in snapshot.children) {
                    val movie = movieSnapshot.getValue(Video::class.java)
                    movie?.let { 
                        movies.add(it)
                        Log.d(TAG, "onDataChange: $it")
                    }
                }
                trySend(movies)
            }

            override fun onCancelled(error: DatabaseError) {

                Log.d(TAG, "onCancelled: ${error.message}")
            }
        }

        databaseReference.addValueEventListener(valueEventListener)
        awaitClose { databaseReference.removeEventListener(valueEventListener) }
    }

}