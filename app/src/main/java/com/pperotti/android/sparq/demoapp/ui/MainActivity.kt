package com.pperotti.android.sparq.demoapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pperotti.android.sparq.demoapp.ui.navigation.SetupNavigation
import com.pperotti.android.sparq.demoapp.ui.theme.SparqDemoAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            SparqDemoAppTheme {
                SetupNavigation()
            }
        }
    }
}
