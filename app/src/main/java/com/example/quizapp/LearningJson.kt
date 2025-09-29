package com.example.quizapp

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import kotlin.math.abs

class LearningJson : AppCompatActivity() {

    companion object {
        val TAG = "LearningJSON"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_learning_json)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // lod from a JSON file
        val gson = Gson()
        val inputStream = resources.openRawResource(R.raw.pluslife)
        val jsonString = inputStream.bufferedReader().use {
            it.readText()
        }
        val test = gson.fromJson(jsonString, PluslifeTest::class.java)
        Log.d(TAG, "onCreate: $test")

        var max: Double = Double.MIN_VALUE
        var min: Double = Double.MAX_VALUE
        var largestDiff: Double = 0.0
        var largestDiffIndex: Int = 0
        var diffCount: Int = 0
        var total: Double = 0.0
        for (i in test.testData.temperatureSamples.indices) {
            if (test.testData.temperatureSamples[i].temp > max) max = test.testData.temperatureSamples[i].temp
            if (test.testData.temperatureSamples[i].temp < min) min = test.testData.temperatureSamples[i].temp
            if (abs(test.testData.temperatureSamples[i].temp - test.targetTemp) > largestDiff) {
                largestDiff = abs(test.testData.temperatureSamples[i].temp - test.targetTemp)
                largestDiffIndex = i
            }
            if (abs(test.testData.temperatureSamples[i].temp - test.targetTemp) > 0.5) diffCount++
            total += test.testData.temperatureSamples[i].temp
        }
        var avg: Double = total / test.testData.temperatureSamples.size
        Log.d(TAG, "onCreate: $max $min $avg $largestDiff $largestDiffIndex $diffCount")
    }
}