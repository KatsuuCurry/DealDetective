package com.the_stilton_assistants.dealdetective

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.the_stilton_assistants.dealdetective.ui.navigation.NavController
import com.the_stilton_assistants.dealdetective.ui.theme.DealDetectiveTheme

class MainActivity : ComponentActivity() {
    lateinit var activityContainer: IActivityContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        activityContainer = ActivityContainer(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DealDetectiveTheme {
                NavController()
            }
        }
    }
}
