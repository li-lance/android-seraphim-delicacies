package com.seraphim.yxsg.ui.page

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

/**
 * 分段环形图（最大值 20，单位 1）
 * - 一圈分成 [max] 个扇段（默认 20），每个扇段表示 1 单位
 * - [value] 支持 0..max 的整数，也支持小数（将绘制部分填充）
 * - [gapAngle] 控制段之间的缝隙角度
 * - [segmentThickness] 控制环厚度
 */
@Composable
fun SegmentedRingChart(
    modifier: Modifier = Modifier,
    value: Float,
    max: Int = 20,
    segmentThickness: Dp = 16.dp,
    gapAngle: Float = 4f,
    startAngle: Float = -90f,
    activeColor: Color = Color(0xFF4CAF50),
    inactiveColor: Color = Color(0xFFE0E0E0),
    roundCaps: Boolean = true,
    showCenterText: Boolean = true,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
    valueFormatter: (Float, Int) -> String = { v, m -> "${v.toInt()}/$m" },
) {
    val clamped = value.coerceIn(0f, max.toFloat())
    val animatedValue by animateFloatAsState(targetValue = clamped, label = "segmented_ring_progress")

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val stroke = Stroke(
                width = segmentThickness.toPx(),
                cap = if (roundCaps) StrokeCap.Round else StrokeCap.Butt
            )

            // 方案1：底层整圈灰色，前景单一激活弧度，绝对平滑
            // 1) 底环（整圈）
            drawCircle(
                color = inactiveColor,
                style = stroke
            )

            // 2) 前景进度弧
            val sweep = (animatedValue / max.toFloat()) * 360f
            val epsilon = 0.1f
            when {
                sweep >= 360f - epsilon -> {
                    // 满圈时直接画整圈，避免 360f arc 的边界问题
                    drawCircle(
                        color = activeColor,
                        style = stroke
                    )
                }
                sweep > 0f -> {
                    drawArc(
                        color = activeColor,
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        style = stroke,
                        size = Size(size.width, size.height)
                    )
                }
                else -> Unit
            }
        }

        if (showCenterText) {
            Text(
                text = valueFormatter(animatedValue, max),
                style = textStyle
            )
        }
    }
}

/**
 * 饼图数据项（value 单位按 1 计，建议总和 <= max(默认 20)）
 */
data class PieItem(
    val label: String,
    val value: Int,
    val color: Color
)

/**
 * 饼图（默认以 20 为满刻度）
 * - [items] 各项值的和不必等于 [max]；若小于 [max]，可选择显示剩余“空白”区
 * - [strokeWidth] 为空时绘制实心饼；不为空时绘制环形饼
 * - [showRemainder] 为 true 且 sum < max 时，绘制剩余部分
 */
@Composable
fun PieChart(
    items: List<PieItem>,
    max: Int = 20,
    modifier: Modifier = Modifier,
    startAngle: Float = -90f,
    strokeWidth: Dp? = null,
    showRemainder: Boolean = true,
    remainderColor: Color = Color(0xFFE0E0E0)
) {
    val total = items.sumOf { it.value }
    val clampedTotal = max(0, min(total, max))
    val hasRemainder = showRemainder && clampedTotal < max
    val remainder = max - clampedTotal

    Canvas(modifier = modifier) {
        val style = strokeWidth?.let {
            Stroke(width = it.toPx(), cap = StrokeCap.Butt)
        }

        var currentStart = startAngle

        fun drawSlice(color: Color, sweep: Float) {
            drawArc(
                color = color,
                startAngle = currentStart,
                sweepAngle = sweep,
                useCenter = style == null, // 实心饼 useCenter = true，环饼 useCenter = false
//                style = style,
                size = Size(size.width, size.height)
            )
            currentStart += sweep
        }

        // 绘制各项
        items.forEach { item ->
            val sweep = (item.value.toFloat() / max.toFloat()) * 360f
            if (sweep > 0f) drawSlice(item.color, sweep)
        }

        // 剩余项
        if (hasRemainder && remainder > 0) {
            val sweep = (remainder.toFloat() / max.toFloat()) * 360f
            drawSlice(remainderColor, sweep)
        }
    }
}

/* ========================= 示例演示 ========================= */

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun SegmentedRingChartPreview() {
    MaterialTheme {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "分段环形图（单位为 1，最大值 20）",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 12/20
                    SegmentedRingChart(
                        value = 12f,
                        max = 20,
                        segmentThickness = 16.dp,
                        gapAngle = 6f,
                        activeColor = Color(0xFF4CAF50),
                        inactiveColor = Color(0xFFE0E0E0),
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                            .aspectRatio(1f)
                    )

                    // 7.5/20，演示部分填充
                    SegmentedRingChart(
                        value = 1f,
                        max = 20,
                        segmentThickness = 20.dp,
                        gapAngle = 4f,
                        activeColor = Color(0xFF3F51B5),
                        inactiveColor = Color(0xFFE0E0E0),
                        roundCaps = true,
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                            .aspectRatio(1f),
                        valueFormatter = { v, m -> "${"%.1f".format(v)}/$m" }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PieChartPreview() {
    MaterialTheme {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "饼图（总量按 20 计）",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 实心饼：总和 15/20，剩余用灰色表示
                    PieChart(
                        items = listOf(
                            PieItem("A", 6, Color(0xFFEF5350)),
                            PieItem("B", 4, Color(0xFF42A5F5)),
                            PieItem("C", 5, Color(0xFF66BB6A)),
                        ),
                        max = 20,
                        startAngle = -90f,
                        strokeWidth = null, // 实心
                        showRemainder = true,
                        remainderColor = Color(0xFFE0E0E0),
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                            .aspectRatio(1f)
                    )

                    // 环形饼：总和 20/20，无剩余
                    PieChart(
                        items = listOf(
                            PieItem("A", 8, Color(0xFFFFA726)),
                            PieItem("B", 7, Color(0xFFAB47BC)),
                            PieItem("C", 5, Color(0xFF29B6F6)),
                        ),
                        max = 20,
                        startAngle = -90f,
                        strokeWidth = 20.dp, // 环形
                        showRemainder = false,
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                            .aspectRatio(1f)
                    )
                }
            }
        }
    }
}