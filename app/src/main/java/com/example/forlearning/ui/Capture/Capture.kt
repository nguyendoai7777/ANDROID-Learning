package com.example.forlearning.ui.Capture

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Space
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.forlearning.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CameraView(
    executor: Executor,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    // UI state
    var lensFacing by remember {
        mutableStateOf(CameraSelector.LENS_FACING_BACK)
    }
    var imageSrc = remember {
        mutableStateOf<String?>(null)
    }

    // variable
    val context = LocalContext.current
    val previewView = remember { PreviewView(context) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val preview = Preview.Builder().build()
    val imageCapture: ImageCapture = remember {
        ImageCapture.Builder().setTargetAspectRatio(AspectRatio.RATIO_16_9).build()

    }
    val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

    // func
    fun changeCamType() {
        lensFacing =
            if (CameraSelector.LENS_FACING_FRONT == lensFacing) CameraSelector.LENS_FACING_BACK else CameraSelector.LENS_FACING_FRONT
    }

    // lifecycle
    LaunchedEffect(lensFacing) {
        Log.d("on","init")
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner, cameraSelector, preview, imageCapture
        )

        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    // UI
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.fillMaxSize()
    ) {
        AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton({ changeCamType() }) {
                Icon(
                    painterResource(R.drawable.ic_switch),
                    tint = Color.White,
                    contentDescription = "",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(1.dp)
                )
            }
            IconButton(
                modifier = Modifier.padding(bottom = 20.dp),
                onClick = {
                    takePhoto(
                        imageCapture = imageCapture,
                        executor = executor,
                        onImageCaptured = {
                            onImageCaptured(it)
                            Log.d("uri: ", it.toString())
                            imageSrc.value = it.toString()
                        },
                        context = context,
                        onError = onError
                    )
                },
                content = {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Take picture",
                        tint = Color.White,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(1.dp)
                            .border(1.dp, Color.White, RoundedCornerShape(50))
                    )
                })
            /*if(imageSrc != null) {
                IconButton(

                    onClick = {}) {
                    GlideImage(
                        model = imageSrc,
                        contentDescription = "",
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else*/
                Text("$imageSrc", Modifier.size(24.dp))
        }
    }
}


private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    executor: Executor,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {

    val fileName = "yyyy-MM-dd-HH-mm-ss-SSS"
    val picType = "image/jpeg"
    val name = SimpleDateFormat(fileName, Locale.US).format(System.currentTimeMillis())
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, picType)
        val appName = R.string.app_name
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/${appName}")
    }

    val outputOptions = ImageCapture.OutputFileOptions.Builder(
        context.contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
    ).build()

    // this is take and save image
    imageCapture.takePicture(outputOptions, executor, object : ImageCapture.OnImageSavedCallback {
        override fun onError(exception: ImageCaptureException) {
            onError(exception)
        }

        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            val savedUri = outputFileResults.savedUri // Uri.fromFile(photoFile)
            Log.d("from delegate func ", savedUri.toString())
            onImageCaptured(savedUri!!)
        }
    })
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine {
    ProcessCameraProvider.getInstance(this).also { cameraProvider ->
        cameraProvider.addListener({
            it.resume(cameraProvider.get())
        }, ContextCompat.getMainExecutor(this))
    }
}