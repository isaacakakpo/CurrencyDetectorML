package com.example.ceditect.ui

import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.util.Size
import androidx.camera.core.ImageProxy
import androidx.core.graphics.toRectF
import androidx.lifecycle.ViewModel
import com.example.ceditect.listeners.DetectListener
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.automl.FirebaseAutoMLRemoteModel
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceAutoMLImageLabelerOptions
import com.google.firebase.ml.vision.objects.FirebaseVisionObject
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions
import de.crysxd.cameraXTracker.ThreadedImageAnalyzer
import de.crysxd.cameraXTracker.ar.ArObject
import de.crysxd.cameraXTracker.ar.ArObjectTracker
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean



class ClassifyCediImageAnalyzer : ViewModel(), ThreadedImageAnalyzer {

    var detectListener: DetectListener? = null
    var labelInfo:String? = null
    var confidence:Float? = null
    private var modelDownloaded = false
    private var bitmap: Bitmap? = null
    private lateinit var labeler: FirebaseVisionImageLabeler
    private var imageFirebase: FirebaseVisionImage? = null





    val remoteModel = FirebaseAutoMLRemoteModel.Builder("Cedi_Tect_20203793036").build()


    val conditions = FirebaseModelDownloadConditions.Builder()
        .build()

    fun downloadCediModel(){

        FirebaseModelManager.getInstance().download(remoteModel, conditions)
            .addOnSuccessListener {
                modelDownloaded = true
                detectListener?.onModelDownloadComplete("Model Downloaded Successfully")
                return@addOnSuccessListener

            }
            .addOnFailureListener {
                detectListener?.onFailure("Failed Downloading model")
                return@addOnFailureListener
            }

    }

    fun isModelDownloaded(){
        FirebaseModelManager.getInstance().isModelDownloaded(remoteModel)
            .addOnSuccessListener {
                modelDownloaded = true


                val labelerOptions = FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder(remoteModel)
                    .setConfidenceThreshold(0.7f)
                    .build()
                labeler = FirebaseVision.getInstance().getOnDeviceAutoMLImageLabeler(labelerOptions)



            }
            .addOnFailureListener {

                detectListener?.onFailure("Failed Downloading model")
                return@addOnFailureListener

            }
    }



    val arObjectTracker = ArObjectTracker()
    private val isBusy = AtomicBoolean(false)
    private val handlerThread = HandlerThread("ClassifyCediImageAnalyzer").apply { start() }
    private val uiHandler = Handler(Looper.getMainLooper())
    private val objectDetector = FirebaseVision.getInstance().getOnDeviceObjectDetector(
        FirebaseVisionObjectDetectorOptions.Builder()
            .setDetectorMode(FirebaseVisionObjectDetectorOptions.STREAM_MODE)
            .enableClassification()
            .build()
    )



    override fun getHandler() = Handler(handlerThread.looper)

    override fun analyze(image: ImageProxy, rotationDegrees: Int) {
        if (image.image != null && isBusy.compareAndSet(false, true)) {
            // Create an FirebaseVisionImage. The image's bytes are in YUV_420_888 format (camera2 API)
            // If you use camera (deprecated, pre L) instead of camera2, use:
            // FirebaseVisionImage.fromByteArray(data, FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
            // and pass the bytes from the Camera.PreviewCallback. Pay attention to rotation of the image in this case!
            val rotation = rotationDegreesToFirebaseRotation(rotationDegrees)
            val visionImage = FirebaseVisionImage.fromMediaImage(image.image!!, rotation)
            val imageSize = Size(image.width, image.height)
            objectDetector.processImage(visionImage).addOnCompleteListener {
                isBusy.set(false)
                // Error? Log it :(
                if (it.exception != null) {
                    Timber.e(it.exception)
                    return@addOnCompleteListener
                }
                // Get the first object of CATEGORY_FASHION_GOOD (first = most prominent) or the already tracked object
                val o = it.result?.firstOrNull { o ->
                    o.classificationCategory == FirebaseVisionObject.CATEGORY_UNKNOWN && o.trackingId != null
                }
                // Hand the object to the tracker. It will interpolate the path and ensure a fluent visual even if we dropped
                // frames because the detection was too slow
                uiHandler.post {
                    arObjectTracker.processObject(
                        if (o != null) {
                            ArObject(
                                trackingId = o.trackingId ?: -1,
                                boundingBox = o.boundingBox.toRectF(),
                                sourceSize = imageSize,
                                sourceRotationDegrees = rotationDegrees
                            )
                        } else {
                            null
                        }

                    )

                    if (o != null) {
                        val boundingBox = o!!.boundingBox
                        val createdBitmap = Bitmap.createBitmap(
                            visionImage.bitmap,
                            boundingBox.left,
                            boundingBox.top,
                            boundingBox.width(),
                            boundingBox.height()
                        )
                        if (createdBitmap.width > MAX_IMAGE_WIDTH) {
                            val dstHeight =
                                (MAX_IMAGE_WIDTH.toFloat() / createdBitmap.width * createdBitmap.height).toInt()
                            bitmap = Bitmap.createScaledBitmap(
                                createdBitmap,
                                MAX_IMAGE_WIDTH,
                                dstHeight, /* filter= */
                                false
                            )
                            imageFirebase = FirebaseVisionImage.fromBitmap(bitmap!!)

                        }

                        imageFirebase = FirebaseVisionImage.fromBitmap(createdBitmap)


                    }

                    if (imageFirebase !=null){
                        labeler.processImage(imageFirebase!!)
                            .addOnSuccessListener { labels ->
                                for (label in labels) {
                                    val detectedNote = label.text
                                    confidence = label.confidence
                                    labelInfo = detectedNote

                                }

                            }
                    }

                }


                if(confidence != null && confidence!! > 0.8){
                    uiHandler
                    Log.d("tag", "Value: " + labelInfo)
                    if (labelInfo!= null) {
                        detectListener?.onSuccess(labelInfo!!,confidence!!.toString())
                    }

                }



            }
        }
    }

    private fun rotationDegreesToFirebaseRotation(rotationDegrees: Int) = when (rotationDegrees) {
        0 -> FirebaseVisionImageMetadata.ROTATION_0
        90 -> FirebaseVisionImageMetadata.ROTATION_90
        180 -> FirebaseVisionImageMetadata.ROTATION_180
        270 -> FirebaseVisionImageMetadata.ROTATION_270
        else -> throw IllegalArgumentException("Rotation $rotationDegrees not supported")
    }

    companion object {
        private const val TAG = "DetectedObject"
        private const val MAX_IMAGE_WIDTH = 640
    }
}