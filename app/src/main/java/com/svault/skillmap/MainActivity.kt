package com.svault.skillmap

import android.graphics.*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.svault.skillmap.ui.theme.SkillMapTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SkillMapTheme {
                Scaffold(modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        FloatingActionButton(shape = ShapeDefaults.ExtraLarge, onClick = {
                            // redirect to add/edit screen
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Skill")
                        }
                    }) { paddingValues ->
                    SkillMap(paddingValues)
                }
            }
        }
    }
}

data class Skill(
    val id: Int = Random.nextInt(),
    val pointX: Float,
    val pointY: Float,
    val name: String
)


@Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
@Composable
fun SkillMap(paddingValues: PaddingValues) {
    var skills by remember { mutableStateOf<List<Skill>>( emptyList()) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        Text(text = "Learning Map", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Text(text = "Tap any skill to edit", fontWeight = FontWeight.Normal)
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val width = constraints.maxWidth.toFloat()
            val height = constraints.maxHeight.toFloat()

            LaunchedEffect(width, height) {
                skills = getInitialSkills(width, height)
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                skills.forEach { skill ->
                    drawCircle(
                        color = Color.Blue,
                        radius = 100f,
                        center = Offset(skill.pointX, skill.pointY)
                    )

                    drawContext.canvas.nativeCanvas.apply {
                        val paintValue = Paint()
                        val paintColor = android.graphics.Color.WHITE
                        val paintTextAlign = Paint.Align.CENTER
                         val paint = paintValue.apply {
                             color = paintColor
                             textSize = 20f
                             textAlign = paintTextAlign
                         }
                        drawText(
                            skill.name,
                            skill.pointX,
                            skill.pointY,
                            paint
                        )
                    }
                }
            }
        }
    }
}

fun getInitialSkills(width: Float, height: Float): List<Skill>{
    return List(5){ index ->
        Skill(
            name = "Skill $index",
            pointX = Random.nextFloat() * width,
            pointY = Random.nextFloat() * height
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VisualMapPreview() {
    SkillMapTheme {
        SkillMap(paddingValues = PaddingValues())
    }
}