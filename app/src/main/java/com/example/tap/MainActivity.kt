package com.example.tap

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.tap.util.Constants.Companion.TIME_FOR_INTERACTION
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var arrVideoUrl: ArrayList<String>
    private lateinit var mainHandler: Handler
    private var currentPosition = 0
    private var maxProgress = TIME_FOR_INTERACTION
    private var rangeOfButtonMotion = 0..0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainHandler = Handler(Looper.getMainLooper())

        arrVideoUrl = ArrayList()
        arrVideoUrl.add(
            "https://assets.mixkit.co/videos/preview/mixkit-sun-over-hills-1183-large.mp4"
        )
        arrVideoUrl.add(
            "https://assets.mixkit.co/videos/preview/mixkit-tree-with-yellow-flowers-1173-large.mp4"
        )
        arrVideoUrl.add(
            "https://assets.mixkit.co/videos/preview/mixkit-man-under-multicolored-lights-1237-large.mp4"
        )

        setVideoData(arrVideoUrl[currentPosition])

        btn_next_video.setOnClickListener {
            pb_download.visibility = View.VISIBLE
            btn_repeat.visibility = View.INVISIBLE
            visibilityDuringNotPlaying()
            enableProgress(null)
            if (currentPosition + 1 == arrVideoUrl.size) currentPosition = -1
            setVideoData(arrVideoUrl[++currentPosition])
        }

        btn_repeat.setOnClickListener {
            pb_download.visibility = View.VISIBLE
            btn_repeat.visibility = View.INVISIBLE
            visibilityDuringNotPlaying()
            setVideoData(arrVideoUrl[currentPosition])
        }
    }

    private fun setVideoData(videoUrl: String) {
        video_view.setVideoPath(videoUrl)

        video_view.setOnPreparedListener { mp ->
            mp.start()
            pb_download.visibility = View.INVISIBLE
            enableProgress(mp.duration - TIME_FOR_INTERACTION)
        }

        video_view.setOnCompletionListener {
            enableProgress(null)
            btn_repeat.visibility = View.VISIBLE
            visibilityDuringNotPlaying()
        }
    }

    private fun visibilityDuringPlaying() {
        pb_download.visibility = View.INVISIBLE
        btn_repeat.visibility = View.INVISIBLE
        tv_left_indicator.visibility = View.VISIBLE
        tv_right_indicator.visibility = View.VISIBLE
        pb_left.visibility = View.VISIBLE
        pb_right.visibility = View.VISIBLE
        btn_next_video.visibility = View.VISIBLE
    }

    private fun visibilityDuringNotPlaying() {
        tv_left_indicator.visibility = View.INVISIBLE
        tv_right_indicator.visibility = View.INVISIBLE
        pb_left.visibility = View.INVISIBLE
        pb_right.visibility = View.INVISIBLE
        btn_next_video.visibility = View.INVISIBLE
    }

    private fun getProgress() =
        if (video_view.currentPosition - video_view.duration + maxProgress > 0)
            video_view.currentPosition - video_view.duration + maxProgress
        else 0

    private fun getRangeOfButtonMotion(): IntRange {
        val widthButton = btn_next_video.width
        val xOfLeftIndicator = tv_left_indicator.x.toInt()
        val xOfRightIndicator = tv_right_indicator.x.toInt()

        return (xOfLeftIndicator + 50..xOfRightIndicator - widthButton - 50)
    }

    private fun getRelativeProgress() = getProgress().toDouble() / maxProgress.toDouble()

    private fun getLocationButton() = rangeOfButtonMotion.first + getRelativeProgress() * (rangeOfButtonMotion.last - rangeOfButtonMotion.first)

    private fun updateProgress() {
        val progress = getProgress()
        val progressInPercentages = ((progress.toDouble() / maxProgress.toDouble()) * 100.0).toInt().toString()
        pb_left.setProgress(progress)
        pb_right.setProgress(progress)
        tv_left_indicator.text = progressInPercentages
        tv_right_indicator.text = progressInPercentages
    }

    private fun updateMaxProgress() {
        pb_left.setMaxProgress(maxProgress)
        pb_right.setMaxProgress(maxProgress)
    }

    private fun updateButtonLocation(location: Double) {
        btn_next_video.x = location.toFloat()
    }

    private fun enableProgress(delay: Int?) {
        updateMaxProgress()
        rangeOfButtonMotion = getRangeOfButtonMotion()

        val runnerForPB = object : Runnable {
            override fun run() {
                updateProgress()
                updateButtonLocation(getLocationButton())
                mainHandler.postDelayed(this, 12)
            }
        }
        val runnerForVisibility = Runnable { visibilityDuringPlaying() }
        if (delay != null) {
            if (delay > 0) {
                mainHandler.postDelayed(runnerForPB, delay.toLong())
                mainHandler.postDelayed(runnerForVisibility, delay.toLong())
            } else mainHandler.post(runnerForPB)
        } else mainHandler.removeCallbacksAndMessages(null)
    }
}