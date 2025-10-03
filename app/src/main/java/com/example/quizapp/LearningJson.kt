package com.example.quizapp

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import kotlin.math.*

class LearningJson : AppCompatActivity() {

    companion object {
        const val TAG = "LearningJSON"
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

        // load from a JSON file
        val gson = Gson()
        val inputStream = resources.openRawResource(R.raw.pluslife)
        val jsonString = inputStream.bufferedReader().use {
            it.readText()
        }
        val test = gson.fromJson(jsonString, PluslifeTest::class.java)
        Log.d(TAG, "onCreate: $test")

        var max = Double.MIN_VALUE
        var min = Double.MAX_VALUE
        var maxIndex = 0
        var minIndex = 0

        var diffCount = 0
        var total = 0.0

        val channelList = mutableListOf<Int>()
        val channelListDiffs = mutableListOf<Int>()
        var increasing = true

        val topThreeDiffs = mutableListOf(Int.MIN_VALUE, Int.MIN_VALUE, Int.MIN_VALUE)

        for (i in test.testData.temperatureSamples.indices) {
            if (test.testData.temperatureSamples[i].temp > max) {
                max = test.testData.temperatureSamples[i].temp
                maxIndex = i
            }
            if (test.testData.temperatureSamples[i].temp < min) {
                min = test.testData.temperatureSamples[i].temp
                minIndex = i
            }

//            if (abs(test.testData.temperatureSamples[i].temp - test.targetTemp) > largestDiff) {
//                largestDiff = abs(test.testData.temperatureSamples[i].temp - test.targetTemp)
//                largestDiffIndex = i
//            }

            if (abs(test.testData.temperatureSamples[i].temp - test.targetTemp) > 0.5) diffCount++
            total += test.testData.temperatureSamples[i].temp
        }

        val largestDiff = max(abs(test.targetTemp - max), abs(test.targetTemp - min))
        val largestDiffIndex = if (abs(test.targetTemp - max) > abs(test.targetTemp - min)) maxIndex else minIndex

        val avg = total / test.testData.temperatureSamples.size

        for (sample in test.testData.samples) {
            if (sample.startingChannel == 3) channelList.add(sample.firstChannelResult)
        }

        for (i in 1 until channelList.size) {
            if (increasing && channelList[i - 1] > channelList[i]) increasing = false

            channelListDiffs.add(abs(channelList[i] - channelList[i - 1]))
        }

        for (i in channelListDiffs.indices) {
            if (channelListDiffs[i] > topThreeDiffs[0]) {
                topThreeDiffs.add(0, channelListDiffs[i])
                topThreeDiffs.removeAt(3)
            }
            else if (channelListDiffs[i] > topThreeDiffs[1]) {
                topThreeDiffs.add(1, channelListDiffs[i])
                topThreeDiffs.removeAt(3)
            }
            else if (channelListDiffs[i] > topThreeDiffs[2]) {
                topThreeDiffs.add(2, channelListDiffs[i])
                topThreeDiffs.removeAt(3)
            }
        }

        Log.d(TAG, "Max: $max\nMin: $min\nAvg: $avg\nLargestDiff: $largestDiff\nLargestDiffIndex: $largestDiffIndex\nDiffCount: $diffCount\nIncreasing: $increasing\nTopThreeDiffs: $topThreeDiffs")
    }
}
