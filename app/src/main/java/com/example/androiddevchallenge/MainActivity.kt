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

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Backspace
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.View.COUNTDOWN
import com.example.androiddevchallenge.View.SETUP
import com.example.androiddevchallenge.ui.theme.MyTheme

enum class View {
    SETUP,
    COUNTDOWN
}

class MainActivity : AppCompatActivity() {
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                Scaffold(topBar = { AppBar(modifier = Modifier.fillMaxWidth()) }) {
                    var view by rememberSaveable { mutableStateOf(SETUP) }
                    var time by remember { mutableStateOf("") }

                    Crossfade(targetState = view) { viewState ->
                        when (viewState) {
                            SETUP -> MyApp(time, onTimeChange = { time = it }) { view = COUNTDOWN }
                            COUNTDOWN -> {
                                CountDownTimer(time = padd(time)) { state -> view = state }
                                time = ""
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Timer",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h6,
            modifier = Modifier.fillMaxWidth(0.9f)
        )
        Icon(imageVector = Icons.Default.Settings, contentDescription = "")
    }
}

private fun padd(time: String): String {
    val diff = 6 - time.length
    return "0".repeat(diff) + time
}

// Start building your app here!
@ExperimentalAnimationApi
@Composable
fun MyApp(time: String = "", onTimeChange: (String) -> Unit = {}, onClick: (View) -> Unit) {
    Surface(color = MaterialTheme.colors.background) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            val paddedTime = if (time.length < 6) {
                val diff = 6 - time.length
                "0".repeat(diff) + time
            } else time

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontSize = 48.sp, letterSpacing = 4.sp)) {
                            append(paddedTime.substring(0, 2))
                        }
                        withStyle(style = SpanStyle(fontSize = 24.sp)) {
                            append("h  ")
                        }
                        withStyle(style = SpanStyle(fontSize = 48.sp, letterSpacing = 4.sp)) {
                            append(paddedTime.substring(2, 4))
                        }
                        withStyle(style = SpanStyle(fontSize = 24.sp)) {
                            append("m  ")
                        }
                        withStyle(style = SpanStyle(fontSize = 48.sp, letterSpacing = 4.sp)) {
                            append(paddedTime.substring(4, 6))
                        }
                        withStyle(style = SpanStyle(fontSize = 24.sp)) {
                            append("s  ")
                        }
                    },
                    style = MaterialTheme.typography.h3,
                    color = if (time.isEmpty()) Color.Gray else Color.DarkGray,
                    modifier = Modifier,
                    textAlign = TextAlign.Center
                )
                IconButton(
                    onClick = {
                        if (time.isNotEmpty()) {
                            onTimeChange(time.substring(0, time.length - 1))
                        }
                    },
                    enabled = time.isNotEmpty()
                ) {
                    Icon(imageVector = Icons.Outlined.Backspace, contentDescription = "Back space")
                }
            }
            Spacer(
                modifier = Modifier
                    .padding(top = 64.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(color = Color.Gray)
            )
            KeyPad(modifier = Modifier) {
                if (!(time.isEmpty() && it == "0") && time.length < 6) {
                    onTimeChange(time + it)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(
                visible = time.isNotEmpty(),
            ) {
                Button(
                    onClick = { onClick(COUNTDOWN) },
                    modifier = Modifier.background(
                        color = Color.Black,
                        shape = MaterialTheme.shapes.medium
                    )
                ) {
                    Text(text = "Start", style = MaterialTheme.typography.h6)
                }
            }
        }
    }
}

@Composable
fun KeyPad(modifier: Modifier, onClick: (String) -> Unit) {
    Surface(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            ThreeNumberRow(start = 1, onClick)
            Spacer(modifier = Modifier.height(16.dp))
            ThreeNumberRow(start = 4, onClick)
            Spacer(modifier = Modifier.height(16.dp))
            ThreeNumberRow(start = 7, onClick)
            ZeroRow(onClick)
        }
    }
}

@Composable
fun ZeroRow(onClick: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NumberText(
            text = "0",
            modifier = Modifier
                .clickable { onClick("0") }
                .padding(24.dp)
        )
    }
}

@Composable
fun ThreeNumberRow(start: Int, onClick: (String) -> Unit) {
    var index = start
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        (1..3).forEach { _ ->
            val text = "${index++}"
            NumberText(
                text = text,
                modifier = Modifier
                    .clickable { onClick(text) }
                    .padding(24.dp)
            )
        }
    }
}

@Composable
fun NumberText(text: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = text,
            style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )
    }
}

@ExperimentalAnimationApi
@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp() {}
    }
}

@ExperimentalAnimationApi
@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp() {}
    }
}
