package com.svault.skillmap

import kotlinx.coroutines.flow.Flow

class SkillRepository(
    private val dao: SkillDao
) {
    fun getAllSkills(): Flow<List<ModelSkill>> {
        return dao.getAllSkills()
    }

    suspend fun addSkill(skill: ModelSkill) {
        dao.insertSkill(skill)
    }

    suspend fun updateSkill(skill: ModelSkill) {
        dao.updateSkill(skill)
    }

    suspend fun deleteSkill(skill: ModelSkill) {
        dao.deleteSkill(skill)
    }
}