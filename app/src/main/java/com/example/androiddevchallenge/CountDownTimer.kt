/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.CountDownTimer
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun CountDownTimer(time: String, onStopClicked: (View) -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        val total by remember { mutableStateOf(convertToLong(time)) }
        var seconds by remember { mutableStateOf(total) }
        val sweepAngle by remember { mutableStateOf(Animatable(0f)) }
        val scope = rememberCoroutineScope()

        val timer = remember {
            object : CountDownTimer(seconds, 1) {
                override fun onTick(millisUntilFinished: Long) {
                    seconds = millisUntilFinished

                    val sic: Float = 360.0f - (millisUntilFinished * 360.0f) / total
                    scope.launch { sweepAngle.animateTo(sic) }
                }

                override fun onFinish() {}
            }
        }

        val stroke = with(LocalDensity.current) { Stroke(6.dp.toPx()) }
        val bgStroke = with(LocalDensity.current) { Stroke(1.dp.toPx()) }

        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            Box(modifier = Modifier.padding(16.dp)) {
                DisposableEffect(key1 = Unit) {
                    timer.start()
                    onDispose { timer.cancel() }
                }
                Canvas(
                    modifier = Modifier
                        .height(300.dp)
                        .align(Alignment.Center)
                        .fillMaxWidth()
                ) {

                    val innerRadius = (size.minDimension - stroke.width) / 2
                    val halfSize = size / 2.0f
                    val topLeft = Offset(
                        halfSize.width - innerRadius,
                        halfSize.height - innerRadius
                    )
                    val size = Size(innerRadius * 2, innerRadius * 2)
                    var startAngle = 270f

                    drawArc(
                        color = Color.LightGray,
                        startAngle = 270f,
                        sweepAngle = 360f,
                        size = size,
                        useCenter = false,
                        style = bgStroke,
                        topLeft = topLeft
                    )

                    drawArc(
                        color = Color.Black,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle.value,
                        size = size,
                        useCenter = false,
                        style = stroke,
                        topLeft = topLeft
                    )
                    startAngle += sweepAngle.value
                }

                Column(modifier = Modifier.align(Alignment.Center)) {
                    Text(
                        text = "time",
                        style = MaterialTheme.typography.h5,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    TimeLabel(
                        seconds = seconds,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                var isPaused by remember { mutableStateOf(false) }

                IconButton(
                    onClick = {
                        onStopClicked(View.SETUP)
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Stop,
                        contentDescription = "",
                        modifier = Modifier.size(32.dp)
                    )
                }
                IconButton(
                    onClick = {
                        isPaused = !isPaused
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        imageVector = if (isPaused) Icons.Outlined.PlayArrow else Icons.Outlined.Pause,
                        contentDescription = "",
                        modifier = Modifier.size(32.dp)
                    )
                }
                IconButton(
                    onClick = {
                        timer.cancel()
                        seconds = total
                        timer.start()
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Replay,
                        contentDescription = "",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TimeLabel(seconds: Long, modifier: Modifier = Modifier) {
    val totalSeconds = (seconds / 1000.0f).roundToInt()
    val hr = totalSeconds / 3600
    val min = (totalSeconds - (hr * 3600)) / 60
    val sec = totalSeconds - (min * 60) - (hr * 60 * 60)

    var text = if (hr == 0) "" else "$hr".padStart(2, '0') + " : "
    text += if (min == 0) "" else "$min".padStart(2, '0') + " : "
    text += "$sec".padStart(2, '0')

    Text(
        text = text,
        style = MaterialTheme.typography.h3,
        modifier = modifier
    )
}

private fun convertToLong(time: String): Long {
    val hour = time.substring(0, 2).toInt()
    val min = time.substring(2, 4).toInt()
    val sec = time.substring(4, 6).toInt()

    var res = (hour * 60 * 60).toLong()
    res += min * 60
    res += sec
    return res * 1000
}
