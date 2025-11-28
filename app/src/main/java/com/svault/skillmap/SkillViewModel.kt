package com.svault.skillmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SkillViewModel @Inject constructor(
    private val repository: SkillRepository
): ViewModel() {
    val skills: StateFlow<List<ModelSkill>> = repository.getAllSkills()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addSkill(skill: ModelSkill) {
        viewModelScope.launch {
            repository.addSkill(skill)
        }
    }

    fun updateSkill(skill: ModelSkill) {
        viewModelScope.launch {
            repository.updateSkill(skill)
        }
    }

    fun deleteSkill(skill: ModelSkill) {
        viewModelScope.launch {
            repository.deleteSkill(skill)
        }
    }
}