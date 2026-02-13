package dev.yveskalume.newsappp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.yveskalume.newsappp.ui.navigation.NewsNavHost
import dev.yveskalume.newsappp.ui.theme.NewsApppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewsApppTheme {
                NewsNavHost()
            }
        }
    }
}