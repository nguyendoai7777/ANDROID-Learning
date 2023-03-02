package com.example.forlearning.ui.Capture

import androidx.compose.runtime.Composable
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PreviewViewLastImage(src: String) {
    GlideImage(model = src, contentDescription = "")
}