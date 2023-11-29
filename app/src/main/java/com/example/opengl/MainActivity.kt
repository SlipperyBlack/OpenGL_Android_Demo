package com.example.opengl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.opengl.ui.theme.OpenGLTheme
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OpenGLTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyScreen()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun OpenGLView(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            GLSurfaceView(ctx).apply {
                // additional setup if needed
            }
        },
        update = { view ->
            // Update the view if necessary
        }
    )
}

@Composable
fun MyScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "OpenGL Integration with Jetpack Compose")
        OpenGLView(modifier = Modifier.weight(1f))
        // ... other composables
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    OpenGLTheme {
       MyScreen()
    }
}