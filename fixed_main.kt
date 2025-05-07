package com.example.shareimagedriverandroidouterapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.shareimagedriverandroidouterapp.ui.theme.ShareImageDriverAndroidOuterAppTheme

class MainActivity : ComponentActivity() {

    private var receivedImageUris by mutableStateOf<List<Uri>>(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle the intent that started the activity
        handleIntent(intent)

        setContent {
            ShareImageDriverAndroidOuterAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SharedImagesScreen(
                        imageUris = receivedImageUris,
                        modifier = Modifier.padding(innerPadding),
                        onLaunchFlutter = { launchFlutter() }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        when (intent.action) {
            Intent.ACTION_SEND -> {
                if (intent.type?.startsWith("image/") == true) {
                    // Handle single image being sent
                    (intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM))?.let { uri ->
                        receivedImageUris = listOf(uri)
                    }
                }
            }

            Intent.ACTION_SEND_MULTIPLE -> {
                if (intent.type?.startsWith("image/") == true) {
                    // Handle multiple images being sent
                    intent.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM)?.let { uris ->
                        receivedImageUris = uris.toList()
                    }
                }
            }
        }
    }

    private fun launchFlutter() {
        startActivity(
            FlutterActivity
                .withCachedEngine(MyApplication.FLUTTER_ENGINE_ID)
                .build(this)
        )
    }
}

@Composable
fun SharedImagesScreen(
    imageUris: List<Uri>, 
    modifier: Modifier = Modifier,
    onLaunchFlutter: () -> Unit = {}
) {
    Column(modifier = modifier) {
        if (imageUris.isEmpty()) {
            Text(
                text = "No images shared yet",
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            Text(
                text = "Received ${imageUris.size} image(s)",
                style = MaterialTheme.typography.headlineSmall
            )

            imageUris.forEachIndexed { index, uri ->
                Text(
                    text = "Image ${index + 1}: $uri",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Button(onClick = onLaunchFlutter) {
            Text("Open Flutter App")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SharedImagesPreview() {
    ShareImageDriverAndroidOuterAppTheme {
        SharedImagesScreen(emptyList())
    }
}
