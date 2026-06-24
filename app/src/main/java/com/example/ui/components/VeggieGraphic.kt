package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun VeggieGraphic(id: String, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        when (id.lowercase().trim()) {
            "palak", "coriander" -> {
                // Spinach / Coriander leafy layers
                val path1 = Path().apply {
                    moveTo(cx, cy + h * 0.35f)
                    cubicTo(cx - w * 0.4f, cy - h * 0.2f, cx - w * 0.2f, cy - h * 0.4f, cx, cy - h * 0.35f)
                    cubicTo(cx + w * 0.2f, cy - h * 0.4f, cx + w * 0.4f, cy - h * 0.2f, cx, cy + h * 0.35f)
                }
                drawPath(path = path1, color = Color(0xFF1B5E20)) // Forest Spinach Green

                // Inner Leaf Vein Line
                drawLine(
                    color = Color(0xFF81C784),
                    start = androidx.compose.ui.geometry.Offset(cx, cy + h * 0.3f),
                    end = androidx.compose.ui.geometry.Offset(cx, cy - h * 0.3f),
                    strokeWidth = 4f
                )
                // Small sub leaf
                val path2 = Path().apply {
                    moveTo(cx + 10f, cy + h * 0.2f)
                    cubicTo(cx + w * 0.3f, cy, cx + w * 0.2f, cy - h * 0.2f, cx + 5f, cy - h * 0.1f)
                    cubicTo(cx + w * 0.1f, cy - h * 0.2f, cx + w * 0.2f, cy + h * 0.1f, cx + 10f, cy + h * 0.2f)
                }
                drawPath(path = path2, color = Color(0xFF4CAF50))
            }
            "tomato" -> {
                // Ripe Desi Tomato red globe
                drawCircle(color = Color(0xFFE53935), radius = minOf(w, h) * 0.38f) // Red Tomato
                drawCircle(color = Color(0xFFFF8A80), radius = minOf(w, h) * 0.12f, center = androidx.compose.ui.geometry.Offset(cx - w*0.12f, cy - h*0.12f)) // Gloss highlight

                // Green stem star on top
                val stemPath = Path().apply {
                    moveTo(cx, cy - h * 0.32f)
                    lineTo(cx - w * 0.12f, cy - h * 0.45f)
                    lineTo(cx - w * 0.03f, cy - h * 0.35f)
                    lineTo(cx, cy - h * 0.48f)
                    lineTo(cx + w * 0.03f, cy - h * 0.35f)
                    lineTo(cx + w * 0.12f, cy - h * 0.45f)
                    close()
                }
                drawPath(path = stemPath, color = Color(0xFF2E7D32))
            }
            "onion" -> {
                // Pinkish purple onion bulb
                val onionPath = Path().apply {
                    moveTo(cx, cy - h * 0.38f)
                    cubicTo(cx - w * 0.42f, cy - h * 0.1f, cx - w * 0.35f, cy + h * 0.35f, cx, cy + h * 0.38f)
                    cubicTo(cx + w * 0.35f, cy + h * 0.35f, cx + w * 0.42f, cy - h * 0.1f, cx, cy - h * 0.38f)
                }
                drawPath(path = onionPath, color = Color(0xFFEC407A)) // Light Lavender Pink

                val linesPath = Path().apply {
                    moveTo(cx, cy - h * 0.38f)
                    quadraticTo(cx - w * 0.2f, cy, cx, cy + h * 0.38f)
                    moveTo(cx, cy - h * 0.38f)
                    quadraticTo(cx + w * 0.2f, cy, cx, cy + h * 0.38f)
                }
                drawPath(path = linesPath, color = Color(0xFFAD1457), style = Stroke(width = 3f)) // concentric arcs
            }
            "potato" -> {
                // Brown organic potato
                val potatoPath = Path().apply {
                    moveTo(cx - w * 0.3f, cy - h * 0.2f)
                    cubicTo(cx - w * 0.45f, cy + h * 0.1f, cx - w * 0.2f, cy + h * 0.35f, cx, cy + h * 0.32f)
                    cubicTo(cx + w * 0.32f, cy + h * 0.35f, cx + w * 0.45f, cy + h * 0.05f, cx + w * 0.3f, cy - h * 0.2f)
                    cubicTo(cx + w * 0.22f, cy - h * 0.38f, cx - w * 0.22f, cy - h * 0.38f, cx - w * 0.3f, cy - h * 0.2f)
                }
                drawPath(path = potatoPath, color = Color(0xFFD7CCC8)) // Potato tan

                // Eyes dots
                drawCircle(color = Color(0xFF8D6E63), radius = 4f, center = androidx.compose.ui.geometry.Offset(cx - w*0.15f, cy - h*0.1f))
                drawCircle(color = Color(0xFF8D6E63), radius = 4f, center = androidx.compose.ui.geometry.Offset(cx + w*0.18f, cy + h*0.05f))
                drawCircle(color = Color(0xFF8D6E63), radius = 3.5f, center = androidx.compose.ui.geometry.Offset(cx, cy - h*0.18f))
            }
            "chilli" -> {
                // Curved hot green chilli
                val chilliPath = Path().apply {
                    moveTo(cx - w * 0.25f, cy - h * 0.35f)
                    quadraticTo(cx + w * 0.22f, cy - h * 0.12f, cx + w * 0.05f, cy + h * 0.38f) // outer curve
                    quadraticTo(cx + w * 0.08f, cy, cx - w * 0.22f, cy - h * 0.25f) // inner curve
                    close()
                }
                drawPath(path = chilliPath, color = Color(0xFF2E7D32)) // Bright green

                // Chilli stem
                val capPath = Path().apply {
                    moveTo(cx - w * 0.22f, cy - h * 0.28f)
                    quadraticTo(cx - w * 0.38f, cy - h * 0.42f, cx - w * 0.32f, cy - h * 0.45f)
                    quadraticTo(cx - w * 0.2f, cy - h * 0.35f, cx - w * 0.18f, cy - h * 0.24f)
                }
                drawPath(path = capPath, color = Color(0xFF1B5E20), style = Stroke(width = 6f))
            }
            "mango" -> {
                // Saffron Alphonso Mango
                val mangoPath = Path().apply {
                    moveTo(cx, cy - h * 0.35f)
                    cubicTo(cx - w * 0.42f, cy - h * 0.15f, cx - w * 0.38f, cy + h * 0.32f, cx, cy + h * 0.36f)
                    cubicTo(cx + w * 0.28f, cy + h * 0.36f, cx + w * 0.42f, cy, cx, cy - h * 0.35f)
                }
                drawPath(path = mangoPath, color = Color(0xFFFFB300)) // Golden orange-saffron

                // Stem & Leaf
                val stem = Path().apply {
                    moveTo(cx, cy - h * 0.3f)
                    quadraticTo(cx, cy - h * 0.43f, cx - w * 0.12f, cy - h * 0.45f)
                }
                drawPath(path = stem, color = Color(0xFF5D4037), style = Stroke(width = 4f))

                val mLeaf = Path().apply {
                    moveTo(cx, cy - h * 0.35f)
                    cubicTo(cx + w * 0.22f, cy - h * 0.42f, cx + w * 0.15f, cy - h * 0.20f, cx, cy - h * 0.35f)
                }
                drawPath(path = mLeaf, color = Color(0xFF2E7D32))
            }
            "banana" -> {
                // Curved yellow banana crescent
                val bananaPath = Path().apply {
                    moveTo(cx - w * 0.32f, cy - h * 0.28f)
                    quadraticTo(cx + w * 0.15f, cy + h * 0.38f, cx + w * 0.35f, cy - h * 0.25f)
                    quadraticTo(cx + w * 0.1f, cy + h * 0.18f, cx - w * 0.32f, cy - h * 0.28f)
                }
                drawPath(path = bananaPath, color = Color(0xFFFFD54F)) // ripe yellow

                // Tip/Stem tags
                drawCircle(color = Color(0xFF4E342E), radius = 6f, center = androidx.compose.ui.geometry.Offset(cx - w*0.32f, cy - h*0.28f))
                drawCircle(color = Color(0xFF4E342E), radius = w * 0.03f, center = androidx.compose.ui.geometry.Offset(cx + w*0.35f, cy - h*0.25f))
            }
            "apple" -> {
                // Red kashmiri apple
                val apple = Path().apply {
                    moveTo(cx, cy - h * 0.28f)
                    cubicTo(cx - w * 0.35f, cy - h * 0.35f, cx - w * 0.38f, cy + h * 0.28f, cx, cy + h * 0.35f)
                    cubicTo(cx + w * 0.38f, cy + h * 0.28f, cx + w * 0.35f, cy - h * 0.35f, cx, cy - h * 0.28f)
                }
                drawPath(path = apple, color = Color(0xFFD32F2F))

                // Stem & Leaf
                val stem = Path().apply {
                    moveTo(cx, cy - h * 0.24f)
                    quadraticTo(cx, cy - h * 0.4f, cx + w * 0.08f, cy - h * 0.42f)
                }
                drawPath(path = stem, color = Color(0xFF5D4037), style = Stroke(width = 4f))
            }
            "atta" -> {
                // Flour bag drawing
                val bag = Path().apply {
                    moveTo(cx - w * 0.25f, cy - h * 0.28f)
                    lineTo(cx + w * 0.25f, cy - h * 0.28f)
                    lineTo(cx + w * 0.28f, cy + h * 0.35f)
                    lineTo(cx - w * 0.28f, cy + h * 0.35f)
                    close()
                }
                drawPath(path = bag, color = Color(0xFFD1B79F)) // jute tan

                // Stitching
                drawLine(
                    color = Color(0xFF5D4037),
                    start = androidx.compose.ui.geometry.Offset(cx - w * 0.25f, cy - h * 0.22f),
                    end = androidx.compose.ui.geometry.Offset(cx + w * 0.25f, cy - h * 0.22f),
                    strokeWidth = 3f
                )
            }
            "rice" -> {
                // Rice grains bowl
                val bowl = Path().apply {
                    moveTo(cx - w * 0.32f, cy)
                    quadraticTo(cx, cy + h * 0.42f, cx + w * 0.32f, cy)
                    close()
                }
                drawPath(path = bowl, color = Color(0xFF4E342E)) // brown wood bowl

                // Rice heap
                val heap = Path().apply {
                    moveTo(cx - w * 0.3f, cy)
                    quadraticTo(cx, cy - h * 0.26f, cx + w * 0.3f, cy)
                    close()
                }
                drawPath(path = heap, color = Color(0xFFEEEEEE))
            }
            "oil" -> {
                // Golden cooking oil bottle
                val bottle = Path().apply {
                    moveTo(cx - w * 0.12f, cy - h * 0.4f)
                    lineTo(cx + w * 0.12f, cy - h * 0.4f)
                    lineTo(cx + w * 0.12f, cy - h * 0.2f)
                    lineTo(cx + w * 0.22f, cy - h * 0.1f)
                    lineTo(cx + w * 0.22f, cy + h * 0.38f)
                    lineTo(cx - w * 0.22f, cy + h * 0.38f)
                    lineTo(cx - w * 0.22f, cy - h * 0.1f)
                    lineTo(cx - w * 0.12f, cy - h * 0.2f)
                    close()
                }
                drawPath(path = bottle, color = Color(0xFFFFF176)) // yellow oil body

                // cap
                val cap = Path().apply {
                    moveTo(cx - w * 0.12f, cy - h * 0.4f)
                    lineTo(cx - w * 0.12f, cy - h * 0.45f)
                    lineTo(cx + w * 0.12f, cy - h * 0.45f)
                    lineTo(cx + w * 0.12f, cy - h * 0.4f)
                    close()
                }
                drawPath(path = cap, color = Color(0xFF2E7D32))
            }
            else -> {
                // Default generic basket icon
                drawCircle(color = Color(0xFFC8E6C9), radius = minOf(w, h) * 0.38f)
                drawCircle(color = Color(0xFF4CAF50), radius = minOf(w, h) * 0.15f)
            }
        }
    }
}
