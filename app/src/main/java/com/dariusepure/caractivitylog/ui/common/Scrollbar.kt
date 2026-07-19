package com.dariusepure.caractivitylog.ui.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun VerticalScrollbar(
    scrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    val showScrollbar = scrollState.isScrollInProgress || scrollState.value > 0
    val alpha by animateFloatAsState(
        targetValue = if (showScrollbar) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(4.dp)
            .padding(vertical = 2.dp)
            .alpha(alpha)
    ) {
        val scrollValue = scrollState.value.toFloat()
        val maxValue = scrollState.maxValue.toFloat()
        
        if (maxValue > 0) {
            val viewPortHeight = 1f // Percentage
            val scrollbarHeight = 0.2f // Percentage of viewport
            val scrollPosition = (scrollValue / maxValue) * (1f - scrollbarHeight)

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .fillMaxHeight(scrollbarHeight)
                    .padding(top = (scrollPosition * 300).dp) // Approximate based on maxHeight
                    .width(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
            )
        }
    }
}
