package com.svault.skillmap

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class BaseModule {
    @Provides
    @Singleton
    fun provideSkillDatabase(application: Application): SkillDatabase = SkillDatabase.initDatabase(application)

    @Provides
    @Singleton
    fun provideSkillDao(skillDatabase: SkillDatabase): SkillDao {
        return skillDatabase.skillDao()
    }

    @Provides
    @Singleton
    fun provideSkillRepository(dao: SkillDao): SkillRepository {
        return SkillRepository(dao)
    }
}