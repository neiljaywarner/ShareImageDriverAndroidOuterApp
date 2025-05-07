package com.example.shareimagedriverandroidouterapp

import android.app.Application
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor

class MyApplication : Application() {
    
    companion object {
        const val FLUTTER_ENGINE_ID = "main_engine"
    }
    
    lateinit var flutterEngine: FlutterEngine
    
    override fun onCreate() {
        super.onCreate()
        
        // Prewarm the Flutter engine
        flutterEngine = FlutterEngine(this)
        flutterEngine.dartExecutor.executeDartEntrypoint(
            DartExecutor.DartEntrypoint.createDefault()
        )
        
        // Cache the prewarmed engine
        FlutterEngineCache.getInstance().put(FLUTTER_ENGINE_ID, flutterEngine)
    }
}
