package com.svault.skillmap

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.svault.skillmap.ui.theme.SkillMapTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt
import com.svault.skillmap.ModelSkill as Skill

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SkillMapTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "skill_map"
    ) {
        composable("skill_map") {
            VisualMap(
                onAddClick = {
                    navController.navigate("new_map/-1")
                },
                onSkillClick = { skill ->
                    navController.navigate("new_map/${skill.id}")
                })
        }

        composable(
            route = "new_map/{skill_id}",
            arguments = listOf(navArgument("skill_id") { type = NavType.IntType })
        ) { backStackEntry ->
            val skillId = backStackEntry.arguments?.getInt("skill_id") ?: -1
            NewMap(skillId = skillId, onNavigateBack = {
                navController.popBackStack()
            })
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
@Composable
fun VisualMap(
    onAddClick: () -> Unit,
    onSkillClick: (Skill) -> Unit,
    viewModel: SkillViewModel = hiltViewModel()
) {
    val skills by viewModel.skills.collectAsState()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(shape = ShapeDefaults.ExtraLarge, onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Skill")
            }
        }) { paddingValues ->
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
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                skills.forEach { skill ->
                                    val distance = sqrt(
                                        (offset.x - skill.pointX).pow(2) +
                                                (offset.y - skill.pointY).pow(2)
                                    )

                                    if (distance <= 100f) {
                                        onSkillClick(skill)
                                    }
                                }
                            }
                        }) {
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewMap(
    skillId: Int,
    onNavigateBack: () -> Unit,
    viewModel: SkillViewModel = hiltViewModel()
) {
    val skills by viewModel.skills.collectAsState()
    val currentSkill = skills.find { it.id == skillId }
    var skillName by remember { mutableStateOf("") }
    var sliderPercentage by remember { mutableStateOf(0f) }
    var notes by remember { mutableStateOf("") }

    LaunchedEffect(currentSkill) {
        currentSkill?.let { skill ->
            skillName = skill.name
            sliderPercentage = skill.progress.toFloat()
            notes = skill.notes
        }
    }

    Log.d("DEBUG_:", "name $skillName")
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Add/Edit Skill") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Skill Name")
            OutlinedTextField(
                value = skillName,
                onValueChange = { skillName = it },
                placeholder = { Text("e.g., Jetpack Compose") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text("Progress ${sliderPercentage.roundToInt()}%")
            Slider(
                value = sliderPercentage,
                onValueChange = { sliderPercentage = it },
                valueRange = 0f..100f,
                steps = 0,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    activeTrackColor = Color.Blue,
                    inactiveTrackColor = Color.Gray,
                ),
                thumb = {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(
                                color = Color.White,
                                shape = CircleShape
                            )
                    )
                }
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Just started", fontSize = 12.sp, color = Color.Gray)
                Text("Mastered", fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("Notes")
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                placeholder = { Text("What are you learning?") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    content = {
                        Text("Delete Skill")
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    ), onClick = {
                        currentSkill?.let { viewModel.deleteSkill(currentSkill) }
                        onNavigateBack()
                    })
                Button(
                    content = {
                        Text("Save")
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue
                    ), onClick = {
                        if (skillId != -1 && currentSkill != null) {
                            val updatedSkill = currentSkill.copy(
                                name = skillName,
                                progress = sliderPercentage.toInt()
                            )
                            viewModel.updateSkill(updatedSkill)
                        } else {
                            val skill = Skill(
                                name = skillName,
                                progress = sliderPercentage.toInt(),
                                pointX = (Math.random() * 500).toFloat(),
                                pointY = (Math.random() * 500).toFloat(),
                                notes = notes
                            )
                            viewModel.addSkill(skill)
                        }
                        onNavigateBack()
                    }, enabled = skillName.isNotBlank()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VisualMapPreview() {
    SkillMapTheme {
        AppNavigation()
    }
}