package com.rama.apps.testapplication

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.datasource.HttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.rama.apps.testapplication.databinding.ActivityVideoPlayBinding
import com.rama.apps.testapplication.utils.AppData
import com.rama.apps.testapplication.utils.UserPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VideoPlayActivity : AppCompatActivity() {


    @Inject
    lateinit var userPreferences: UserPreferences

    private var rewardedAd: RewardedAd? = null
    private lateinit var player: ExoPlayer
    private var isFullScreen = false
    private val adUnitId = "ca-app-pub-3940256099942544/5224354917"
    private val adLoadDelayMillis: Long = 20000 // 1 minute

    private val handler = Handler(Looper.getMainLooper())

    private var isAutoPlayEnabled: Boolean = false
    private val adLoadRunnable = Runnable {
        loadRewardedAd()
    }

    private lateinit var binding: ActivityVideoPlayBinding
    private var currentVideoIndex = AppData.videoIndex


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)


        isAutoPlayEnabled = userPreferences.setAutoPlay



        handler.postDelayed(adLoadRunnable, adLoadDelayMillis)


        setupPlayer()
        playVideo(currentVideoIndex)


        binding.rotateButton.setOnClickListener {
            rotatePlayer()
        }



    }


    private fun setupPlayer() {
        player = ExoPlayer.Builder(this).build()

        binding.playerView.player = player




        player.addListener(object : Player.Listener {

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    Log.d(TAG, "onIsPlayingChanged: playing")
                } else {
                    Log.d(TAG, "onIsPlayingChanged: not playing")
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                val cause = error.cause
                if (cause is HttpDataSource.HttpDataSourceException) {
                    if (cause is HttpDataSource.InvalidResponseCodeException) {
                        Log.d(TAG, "onPlayerError: ${cause.cause}")
                    } else {
                        Log.d(TAG, "onPlayerError: ${cause.message}")
                    }
                }
            }

            override fun onTracksChanged(tracks: Tracks) {
                Log.d(TAG, "onTracksChanged: $tracks")
            }
        })
    }


    private fun playVideo(index: Int) {
        if (isAutoPlayEnabled){
            val mediaItems:MutableList<MediaItem> = mutableListOf()
            for (i in index..<AppData.videos.size){
                val url = AppData.videos[i]
                mediaItems.add(MediaItem.fromUri(url))
            }
            player.setMediaItems(mediaItems)
        }else{
            val mediaItem = MediaItem.fromUri(AppData.videos[index])
            player.setMediaItem(mediaItem)
        }


        player.prepare()
        player.play()

    }


    private fun playNextVideo() {
        currentVideoIndex = (currentVideoIndex + 1) % AppData.videos.size
        playVideo(currentVideoIndex)
    }


    private fun loadRewardedAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            this,
            adUnitId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    Log.d(TAG, "onAdFailedToLoad: $p0")
                    rewardedAd = null
                }

                override fun onAdLoaded(p0: RewardedAd) {
                    Log.d(TAG, "Ad was loaded.")
                    rewardedAd = p0

                    rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            Log.d(TAG, "Ad dismissed fullscreen content.")
                            rewardedAd = null
                            resumeVideo()

                            handler.postDelayed(adLoadRunnable, adLoadDelayMillis)
                        }

                        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                            Log.e(TAG, "Ad failed to show fullscreen content.")
                            rewardedAd = null
                            resumeVideo()
                        }
                    }


                    showRewardedAd()
                }
            }
        )
    }


    private fun showRewardedAd() {

        player.playWhenReady = false

        rewardedAd?.show(this@VideoPlayActivity) { rewardItem ->
            val rewardAmount = rewardItem.amount
            val rewardType = rewardItem.type

            Log.d(TAG, "User earned the reward.$rewardAmount")
            Log.d(TAG, "User earned the reward.$rewardType")
        } ?: run {
            Log.d(TAG, "The rewarded ad wasn't ready yet.")
            // Resume video playback if the ad isn't ready
            resumeVideo()
        }
    }

    private fun resumeVideo() {

        if (player.playbackState == Player.STATE_READY && !player.playWhenReady) {
            player.playWhenReady = true
        }
    }


    private fun rotatePlayer() {
        requestedOrientation = if (!isFullScreen) {
            binding.rotateButton.setImageDrawable(
                ContextCompat.getDrawable(applicationContext, R.drawable.ic_fullscreen_exit)
            )
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            binding.rotateButton.setImageDrawable(
                ContextCompat.getDrawable(applicationContext, R.drawable.fullscreen_24)
            )
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        isFullScreen = !isFullScreen
    }


    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
    override fun onPause() {
        super.onPause()

        // Pause player playback
        player.playWhenReady = false



        handler.removeCallbacks(adLoadRunnable)

        // Check if the ad is ready
        if (rewardedAd != null) {
            // If the ad is ready, set the rewardedAd to null
            // This ensures that the ad won't be shown again when the activity is resumed
            rewardedAd = null
        } else {
            // If the ad is not ready, resume video playback
            resumeVideo()
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}