package com.example.forlearning.ui.Capture

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.example.forlearning.R

@Composable
fun CameraView(
    executor: Executor,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    // 1
    var lensFacing by remember {
        mutableStateOf(CameraSelector.LENS_FACING_BACK)
    }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture: ImageCapture = remember {
        ImageCapture.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .build()

    }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()

    // 2
    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )

        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    // 3
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.fillMaxSize()
    ) {
        AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = {
                    lensFacing = if(CameraSelector.LENS_FACING_FRONT == lensFacing) CameraSelector.LENS_FACING_BACK else CameraSelector.LENS_FACING_FRONT
                }
            ) {
                Icon(
                    painterResource(R.drawable.ic_switch),
                    tint = Color.White,
                    contentDescription = "",
                )
            }
            IconButton(
                modifier = Modifier.padding(bottom = 20.dp),
                onClick = {
                    takePhoto(
                        imageCapture = imageCapture,
                        executor = executor,
                        onImageCaptured = onImageCaptured,
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
                }
            )
            IconButton(

                onClick = {}
            ) {
                Icon(painterResource(R.drawable.ic_switch), contentDescription = "")
            }
        }
    }
}

private fun takePhoto(
    imageCapture: ImageCapture,
    executor: Executor,
    onImageCaptured: (Uri) -> Unit,
    context: Context,
    onError: (ImageCaptureException) -> Unit
) {

    val TAG = "CameraXBasic"
    val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
    val PHOTO_TYPE = "image/jpeg"
    val RATIO_4_3_VALUE = 4.0 / 3.0
    val RATIO_16_9_VALUE = 16.0 / 9.0


    val name = SimpleDateFormat(FILENAME, Locale.US)
        .format(System.currentTimeMillis())
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, PHOTO_TYPE)
        val appName = R.string.app_name
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/${appName}")
    }

    val outputOptions = ImageCapture.OutputFileOptions.Builder(
        context.contentResolver,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    ).build()

    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exception: ImageCaptureException) {
                Log.e("kilo", "Take photo error:", exception)
                onError(exception)
            }

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = outputFileResults.savedUri // Uri.fromFile(photoFile)
                onImageCaptured(savedUri!!)
            }
        })
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }