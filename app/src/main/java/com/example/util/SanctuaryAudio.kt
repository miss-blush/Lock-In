package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.exp
import kotlin.math.sin

object SanctuaryAudio {

    fun playFocusChime(context: Context, enabled: Boolean = true) {
        triggerHaptic(context)
        if (!enabled) return

        CoroutineScope(Dispatchers.Default).launch {
            try {
                val sampleRate = 44100
                val durationSeconds = 2.5
                val numSamples = (durationSeconds * sampleRate).toInt()
                val samples = ShortArray(numSamples)

                // Pure harmonic chime frequencies (Singing bowl tuned around 528 Hz Love/Focus frequency & 1056 Hz harmonic)
                val freq1 = 528.0
                val freq2 = 1056.0
                val freq3 = 1584.0

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    // Smooth exponential decay
                    val envelope = exp(-2.2 * t)
                    val wave = (0.7 * sin(2.0 * Math.PI * freq1 * t) +
                            0.25 * sin(2.0 * Math.PI * freq2 * t) +
                            0.1 * sin(2.0 * Math.PI * freq3 * t)) * envelope
                    samples[i] = (wave * Short.MAX_VALUE * 0.75).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()

                val audioFormat = AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()

                val bufferSize = samples.size * 2
                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setAudioFormat(audioFormat)
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(samples, 0, samples.size)
                audioTrack.play()
                Thread.sleep((durationSeconds * 1000).toLong())
                audioTrack.stop()
                audioTrack.release()
            } catch (_: Exception) {
                // Graceful fallback if audio hardware is constrained
            }
        }
    }

    fun triggerHaptic(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.let { vm ->
                    val effect = VibrationEffect.createWaveform(
                        longArrayOf(0, 120, 100, 200),
                        intArrayOf(0, 160, 0, 220),
                        -1
                    )
                    vm.vibrate(CombinedVibration.createParallel(effect))
                    return
                }
            }
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(200)
            }
        } catch (_: Exception) {
        }
    }
}
