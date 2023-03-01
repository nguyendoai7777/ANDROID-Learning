package com.example.forlearning

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.forlearning.ui.theme.ForLearningTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ForLearningTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

data class BoxItem(
    val h: Dp,
    val color: Color
)

@Composable
fun BoxWithRandomColor(item: BoxItem) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(item.h)
            .clip(RoundedCornerShape(8.dp))
            .background(item.color)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Greeting(name: String) {
    val itemsList = (1..100).map { 
        BoxItem(
            h = Random.nextInt(40, 300).dp,
            color = Color(Random.nextLong(0xFFFFFFF)).copy(alpha = 1f)
        )
    }
    LazyVerticalStaggeredGrid(
        // columns = StaggeredGridCells.Fixed(3),
        columns = StaggeredGridCells.Adaptive(50.dp),
        contentPadding = PaddingValues(12.dp),
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(itemsList) {
            BoxWithRandomColor(item = it)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ForLearningTheme {
        Greeting("Android")
    }
}