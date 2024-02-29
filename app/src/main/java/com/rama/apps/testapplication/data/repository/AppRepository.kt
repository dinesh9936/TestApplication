package com.rama.apps.testapplication.data.repository

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rama.apps.testapplication.MainActivity
import com.rama.apps.testapplication.data.model.CountryCode
import com.rama.apps.testapplication.data.model.Video
import com.rama.apps.testapplication.ui.base.AuthState
import com.rama.apps.testapplication.utils.InternetConnectivity.NetworkUtils
import com.rama.apps.testapplication.utils.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userPreferences: UserPreferences
) {


    private var verificationID: String? = null
    private var resendToken: ForceResendingToken? = null

    private var resendOTPTimer: CountDownTimer? = null
    private var onTickCallback: ((Long) -> Unit)? = null
    private var onFinishCallback: (() -> Unit)? = null

    companion object {
        private const val TAG = "AppRepository"
    }

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn


    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> get() = _authState



    private val _countryCode = MutableLiveData<List<CountryCode>>()
    val countryCode: LiveData<List<CountryCode>> get() = _countryCode


    private var countryCodes = listOf(
        CountryCode("+1", "United States"),
        CountryCode("+44", "United Kingdom"),
        CountryCode("+91", "India"),
        CountryCode("+81", "Japan"),
        CountryCode("+86", "China")
    )


    fun checkLoggedIn(){
        val user = firebaseAuth.currentUser
        if (user != null){
            _isLoggedIn.postValue(true)
        }else{
            _isLoggedIn.postValue(false)
        }
    }


    fun getCountryCodes(){
       _countryCode.value = countryCodes
    }

    fun startTimer(
        totalTimeInMillis: Long,
        onTick: (Long) -> Unit,
        onFinish: () -> Unit
    ){
        resendOTPTimer?.cancel()
        resendOTPTimer = object :CountDownTimer(
            totalTimeInMillis, 1000
        ){
            override fun onFinish() {
                onFinishCallback?.invoke()
            }

            override fun onTick(millisUntilFinished: Long) {
                onTickCallback?.invoke(millisUntilFinished / 1000)
            }
        }

        onTickCallback = onTick
        onFinishCallback = onFinish
        resendOTPTimer?.start()
    }


    fun stopTimer(){
        resendOTPTimer?.cancel()
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


    suspend fun sendOtp(phoneNumber: String, activity: MainActivity) = withContext(Dispatchers.IO){
        if (NetworkUtils.isInternetAvailable(activity)){
            _isLoggedIn.postValue(true)
            try {
                _authState.postValue(AuthState.Loading)

                val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber(phoneNumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(activity)
                    .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                            _authState.postValue(AuthState.Authenticated)
                            Log.d(TAG, "onVerificationCompleted: $p0")
                            signInWithPhoneAuthCredential(p0, activity)
                        }

                        override fun onVerificationFailed(e: FirebaseException) {
                            when (e) {
                                is FirebaseAuthInvalidCredentialsException -> {
                                    _authState.postValue(AuthState.Error("Invalid Request"))
                                }

                                is FirebaseTooManyRequestsException -> {
                                    _authState.postValue(AuthState.Error("Too Many Request"))
                                }

                                is FirebaseAuthMissingActivityForRecaptchaException -> {
                                    _authState.postValue(AuthState.Error("reCAPTCHA verification attempted with null Activity"))
                                }
                            }
                        }

                        override fun onCodeSent(
                            verificationId: String,
                            token: ForceResendingToken) {

                            _authState.postValue(AuthState.CodeSent)
                            verificationID = verificationId
                            resendToken = token
                        }

                        override fun onCodeAutoRetrievalTimeOut(message: String) {
                            Log.d(TAG, "onCodeAutoRetrievalTimeOut: $message")
                        }

                    }).build()
                PhoneAuthProvider.verifyPhoneNumber(options)

            } catch (e: Exception) {
                _authState.postValue(AuthState.Error(e.message!!))
            }
        }else{
            _authState.postValue(AuthState.Error("Internet Not Available."))
        }

    }

    suspend fun verifyOtp(verificationCode: String, activity: MainActivity) = withContext(Dispatchers.IO) {
        try {
            _authState.postValue(AuthState.Loading)

            val credential = PhoneAuthProvider.getCredential(verificationID!!, verificationCode)

            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithPhoneAuthCredential: SignIn Success.")
                        userPreferences.setLoggedIn = true
                        _authState.postValue(AuthState.Authenticated)
                    } else {
                        Log.w(TAG, "signInWithPhoneAuthCredential: SignIn Failed.")
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            _authState.postValue(AuthState.Error("You have entered Invalid OTP"))
                        }
                    }
                }


        } catch (e: Exception) {
            _authState.postValue(AuthState.Error(e.message!!))

        }
    }



    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, activity: MainActivity) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(activity){task->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithPhoneAuthCredential: SignIn Success.")
                    _authState.postValue(AuthState.Authenticated)
                    val user = task.result?.user
                }else{
                    Log.w(TAG, "signInWithPhoneAuthCredential: SignIn Failed.")
                    if (task.exception is FirebaseAuthInvalidCredentialsException){
                        _authState.postValue(AuthState.Error("You have entered Invalid OTP"))
                    }
                }

            }
    }

    fun logOut(){
        firebaseAuth.signOut()
        userPreferences.setLoggedIn = false
        _isLoggedIn.postValue(false)
    }

    suspend fun resendCode(
        phoneNumber: String,
        activity: MainActivity) = withContext(Dispatchers.IO){
        try {
            _authState.value = AuthState.Loading
            val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                    override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                        _authState.postValue(AuthState.Authenticated)
                        signInWithPhoneAuthCredential(p0,activity)
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        when (e) {
                            is FirebaseAuthInvalidCredentialsException -> {
                                _authState.postValue(AuthState.Error("Invalid Request"))
                            }

                            is FirebaseTooManyRequestsException -> {
                                _authState.postValue(AuthState.Error("Too Many Request"))
                            }

                            is FirebaseAuthMissingActivityForRecaptchaException -> {
                                _authState.postValue(AuthState.Error("reCAPTCHA verification attempted with null Activity"))
                            }
                        }
                    }

                    override fun onCodeSent(verificationId: String, token: ForceResendingToken) {
                        _authState.postValue(AuthState.CodeSent)
                        verificationID = verificationId
                        resendToken = token
                    }

                }).setForceResendingToken(resendToken!!).build()
        }catch (e: Exception){
            _authState.postValue(AuthState.Error(e.message!!))
        }
    }


}