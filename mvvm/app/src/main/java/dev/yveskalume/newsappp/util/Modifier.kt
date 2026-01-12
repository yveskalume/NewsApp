package dev.yveskalume.newsappp.util

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier

/**
 * To use when consuming padding values from Scaffold
 */
fun Modifier.paddingAndConsumeWindowInsets(
    paddingValues: PaddingValues
) = this
    .padding(paddingValues)
    .consumeWindowInsets(paddingValues)