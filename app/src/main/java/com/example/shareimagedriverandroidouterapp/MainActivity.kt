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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.shareimagedriverandroidouterapp.ui.theme.ShareImageDriverAndroidOuterAppTheme

class MainActivity : ComponentActivity(), Pigeon.ImageHostApi {

    private var receivedImageUris by mutableStateOf<List<Uri>>(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = applicationContext as MyApplication
        Pigeon.ImageHostApi.setUp(app.flutterEngine.dartExecutor.binaryMessenger, this)

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
        val app = applicationContext as MyApplication
        Pigeon.ImageHostApi.setUp(app.flutterEngine.dartExecutor.binaryMessenger, this)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        when (intent.action) {
            Intent.ACTION_SEND -> {
                if (intent.type?.startsWith("image/") == true) {
                    (intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM))?.let { uri ->
                        receivedImageUris = listOf(uri)
                    }
                }
            }
            Intent.ACTION_SEND_MULTIPLE -> {
                if (intent.type?.startsWith("image/") == true) {
                    intent.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM)?.let { uris ->
                        receivedImageUris = uris.toList()
                    }
                }
            }
        }
        if (receivedImageUris.isNotEmpty()) {
            // After handling the intent and updating receivedImageUris,
            // If Flutter is already active, you might want to notify it.
            // However, getSharedImages will be called by Flutter when it needs the data.
        }
    }

    private fun launchFlutter() {
        startActivity(
            FlutterActivity
                .withCachedEngine(MyApplication.FLUTTER_ENGINE_ID)
                .build(this)
        )
    }

    override fun getSharedImages(): MutableList<Pigeon.ImageData> {
        val imageList = mutableListOf<Pigeon.ImageData>()
        for (uri in receivedImageUris) {
            val imageData = Pigeon.ImageData.Builder()
                .setUri(uri.toString())
                .setMimeType(contentResolver.getType(uri) ?: "image/*")
                .build()
            imageList.add(imageData)
        }
        return imageList
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
                text = "No images shared yet. Share an image to this app, then click below.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            Text(
                text = "Received ${imageUris.size} image(s). Click below to open in Flutter.",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )
            imageUris.forEachIndexed { index, uri ->
                Text(
                    text = "Image ${index + 1}: $uri",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }

        Button(onClick = onLaunchFlutter, modifier = Modifier.padding(16.dp)) {
            Text("Open Flutter App")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SharedImagesPreview() {
    ShareImageDriverAndroidOuterAppTheme {
        SharedImagesScreen(listOf(Uri.parse("content://sample/1"), Uri.parse("content://sample/2")))
    }
}