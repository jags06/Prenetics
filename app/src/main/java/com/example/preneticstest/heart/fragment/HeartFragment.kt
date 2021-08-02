package com.example.preneticstest.heart.fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.TextureView.SurfaceTextureListener
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import com.example.preneticstest.R
import com.example.preneticstest.base.fragment.BindingFragment
import com.example.preneticstest.databinding.HeartLayoutBinding
import com.example.preneticstest.heart.viewmodel.HeartViewModel
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.*

class HeartFragment : BindingFragment<HeartLayoutBinding>() {

    private val viewModel: HeartViewModel by inject()
    var wakeLock: WakeLock? = null

    private var textureView: TextureView? = null
    lateinit var cameraId: String
    private var cameraDevice: CameraDevice? = null
    private var cameraCaptureSessions: CameraCaptureSession? = null
    private var captureRequestBuilder: CaptureRequest.Builder? = null
    private var imageDimension: Size? = null
    private val REQUEST_CAMERA_PERMISSION = 1

    // Thread handler member variables
    private var mBackgroundHandler: Handler? = null
    private var mBackgroundThread: HandlerThread? = null

    //Heart rate detector member variables
    var hrtratebpm = 0
    private var mCurrentRollingAverage = 0
    private var mLastRollingAverage = 0
    private var mLastLastRollingAverage = 0
    private val mTimeArray = LongArray(16)
    private var numCaptures = 0
    private var mNumBeats = 0

    lateinit var wakelock: WakeLock

    override fun getLayoutResId(): Int {
        return R.layout.heart_layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.heartViewModel = viewModel
        binding.lifecycleOwner = this
        textureView = binding.texture

        viewModel.saveClickSubject.subscribe {
            viewModel.saveHeartRate(binding.heartRateValueText.text.toString())
        }
    }

    var textureListener: SurfaceTextureListener = object : SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            openCamera()
        }

        override fun onSurfaceTextureSizeChanged(
            surface: SurfaceTexture,
            width: Int,
            height: Int
        ) {
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            return false
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
            Timber.d("onSurfaceTextureUpdated")

            val bmp = textureView!!.bitmap
            val width = bmp!!.width
            val height = bmp.height
            val pixels = IntArray(height * width)
            // Get pixels from the bitmap, starting at (x,y) = (width/2,height/2)
            // and totaling width/20 rows and height/20 columns
            bmp.getPixels(pixels, 0, width, width / 2, height / 2, width / 20, height / 20)
            var sum = 0
            for (i in 0 until height * width) {
                val red = pixels[i] shr 16 and 0xFF
                sum += red
            }
            // Waits 20 captures, to remove startup artifacts.  First average is the sum.
            if (numCaptures == 20) {
                mCurrentRollingAverage = sum
            } else if (numCaptures in 21..48) {
                mCurrentRollingAverage =
                    (mCurrentRollingAverage * (numCaptures - 20) + sum) / (numCaptures - 19)
            } else if (numCaptures >= 49) {
                mCurrentRollingAverage = (mCurrentRollingAverage * 29 + sum) / 30
                if (mLastRollingAverage > mCurrentRollingAverage && mLastRollingAverage > mLastLastRollingAverage && mNumBeats < 15) {
                    if (mTimeArray != null) {
                        mTimeArray[mNumBeats] = System.currentTimeMillis()
                        Timber.d("mNumBeats $mNumBeats : mTimeArray ${mTimeArray[mNumBeats]}")

                    }
                    mNumBeats++
                    if (mNumBeats == 15) {
                        closeCamera()
                        calcBPM()
                    }
                }
            }

            // Another capture
            numCaptures++
            // Save previous two values
            mLastLastRollingAverage = mLastRollingAverage
            mLastRollingAverage = mCurrentRollingAverage
        }
    }
    private val stateCallback: CameraDevice.StateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            Timber.d("Camera open")
            cameraDevice = camera
            createCameraPreview()
        }

        override fun onDisconnected(camera: CameraDevice) {
            cameraDevice!!.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            if (cameraDevice != null) cameraDevice!!.close()
            cameraDevice = null
        }
    }

    // onResume
    protected fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("Camera Background")
        mBackgroundThread!!.start()
        mBackgroundHandler = Handler(mBackgroundThread!!.getLooper())
    }

    // onPause
    private fun stopBackgroundThread() {
        mBackgroundThread!!.quitSafely()
        try {
            mBackgroundThread!!.join()
            mBackgroundThread = null
            mBackgroundHandler = null
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun calcBPM() {
        val med: Int
        val timedist = LongArray(14)
        for (i in 0..13) {
            if (mTimeArray != null) {
                timedist[i] = mTimeArray[i + 1] - mTimeArray[i]
            }
        }
        Arrays.sort(timedist)
        Timber.d("size of timedist ${timedist.size}")
        med = timedist[timedist.size / 2].toInt()
        hrtratebpm = 60000 / med

        viewModel.heartField.set("$hrtratebpm BPM")
        Timber.d("HeartRate is $hrtratebpm")
    }

    private fun createCameraPreview() {
        try {
            val texture = textureView!!.surfaceTexture!!
            imageDimension?.let { texture.setDefaultBufferSize(it.width, imageDimension!!.height) }
            val surface = Surface(texture)
            captureRequestBuilder =
                cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder!!.addTarget(surface)
            cameraDevice!!.createCaptureSession(
                listOf(surface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(@NonNull cameraCaptureSession: CameraCaptureSession) {
                        if (null == cameraDevice) {
                            return
                        }
                        cameraCaptureSessions = cameraCaptureSession
                        updatePreview()
                    }

                    override fun onConfigureFailed(@NonNull cameraCaptureSession: CameraCaptureSession) {
                        Toast.makeText(
                            requireActivity(),
                            "Configuration change",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                null
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    // Opening the rear-facing camera for use
    private fun openCamera() {
        val manager = requireActivity().getSystemService(Context.CAMERA_SERVICE) as CameraManager?
        try {
            cameraId = manager!!.cameraIdList[0]
            val characteristics = manager.getCameraCharacteristics(cameraId)
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
            imageDimension = map.getOutputSizes(SurfaceTexture::class.java)[0]
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION
                )
                return
            }
            manager.openCamera(cameraId, stateCallback, null)
            textureView!!.surfaceTextureListener = textureListener
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        Timber.d("openCamera X")
    }

    private fun updatePreview() {
        if (null == cameraDevice) {
            Timber.d("open peview")
        }
        captureRequestBuilder!!.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
        captureRequestBuilder!!.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH)
        try {
            cameraCaptureSessions!!.setRepeatingRequest(
                captureRequestBuilder!!.build(),
                null,
                mBackgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun closeCamera() {
        if (null != cameraDevice) {
            cameraDevice!!.close()
            cameraDevice = null
        }
    }

    override fun onPause() {
        super.onPause()
        if (wakeLock != null) {
            wakelock?.release()
        }
        binding.heartRateValueText.text = "-- BPM"
        closeCamera()
        stopBackgroundThread()

    }

    override fun onResume() {
        super.onResume()
        binding.heartRateValueText.text = "-- BPM"
        viewModel.getlastHeartRate("peter@prenetics.com")
        val pm = requireActivity().getSystemService(Context.POWER_SERVICE) as PowerManager?
        wakelock = pm!!.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen:12")
        wakeLock?.acquire()
        startBackgroundThread()
        if (textureView!!.isAvailable) {
            openCamera()
        } else {
            textureView!!.surfaceTextureListener = textureListener
        }
    }
}