package com.svault.skillmap

import kotlin.random.Random

data class ModelSkill(
    val id: Int = Random.nextInt(),
    val pointX: Float,
    val pointY: Float,
    val name: String
)
