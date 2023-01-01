package com.shellwoo.newyear

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnRepeat
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.congratulation
import kotlinx.android.synthetic.main.activity_main.rootLayout
import kotlinx.android.synthetic.main.activity_main.santa

class MainActivity : AppCompatActivity() {

	private companion object {

		const val REVERSED_VIEW_SCALE = -1f

		const val SANTA_ANIMATION_DURATION = 10000L
		const val CELEBRATION_ANIMATION_DURATION = 20000L
	}

	private var mediaPlayer: MediaPlayer? = null

	private val santaRestartAnimationLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
		santaAnimator?.cancel()
		setSantaDefaultOrientation()
		startSantaAnimation()
	}

	private var santaAnimator: ObjectAnimator? = null
	private var congratulationAnimator: ObjectAnimator? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		renderSanta()
		initCongratulationAnimator()
		initMediaPlayer()
	}

	private fun renderSanta() {
		Glide.with(this)
			.load(R.raw.santa)
			.into(santa)
	}

	private fun initCongratulationAnimator() {
		congratulationAnimator = ObjectAnimator.ofArgb(
			congratulation,
			"textColor",
			Color.RED, Color.YELLOW, Color.GREEN,
		)
			.setDuration(CELEBRATION_ANIMATION_DURATION)
			.apply {
				interpolator = AccelerateDecelerateInterpolator()
				repeatMode = ValueAnimator.REVERSE
				repeatCount = ValueAnimator.INFINITE
			}
	}

	private fun initMediaPlayer() {
		mediaPlayer = MediaPlayer.create(this, R.raw.merry_christmas)
			.apply { isLooping = true }
	}

	override fun onResume() {
		super.onResume()

		mediaPlayer?.start()
		startAnimation()
	}

	private fun startAnimation() {
		congratulationAnimator?.start()
		santa.viewTreeObserver.addOnGlobalLayoutListener(santaRestartAnimationLayoutListener)
	}

	private fun setSantaDefaultOrientation() {
		santa.scaleX = REVERSED_VIEW_SCALE
	}

	private fun startSantaAnimation() {
		val screenWidth = rootLayout.right.toFloat()
		val santaDiveDepthX = santa.width.toFloat()
		val santaStartX = -santaDiveDepthX
		val santaEndX = screenWidth + santaDiveDepthX

		santaAnimator = ObjectAnimator.ofFloat(santa, "x", santaStartX, santaEndX)
			.setDuration(SANTA_ANIMATION_DURATION)
			.apply {
				interpolator = LinearInterpolator()
				repeatMode = ValueAnimator.REVERSE
				repeatCount = ValueAnimator.INFINITE
				doOnRepeat { reverseSantaOrientation() }
				start()
			}
	}

	private fun reverseSantaOrientation() {
		santa.scaleX = -santa.scaleX
	}

	override fun onPause() {
		super.onPause()

		mediaPlayer?.pause()
		santa.viewTreeObserver.removeOnGlobalLayoutListener(santaRestartAnimationLayoutListener)
		stopAnimation()
	}

	private fun stopAnimation() {
		congratulationAnimator?.cancel()
		santaAnimator?.cancel()
	}

	override fun onDestroy() {
		super.onDestroy()
		mediaPlayer?.stop()
	}
}