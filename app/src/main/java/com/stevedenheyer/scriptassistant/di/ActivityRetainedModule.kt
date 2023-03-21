package com.stevedenheyer.scriptassistant.di

import com.stevedenheyer.scriptassistant.common.data.room.repositories.AudioRepositoryImpl
import com.stevedenheyer.scriptassistant.common.data.room.repositories.ProjectRepositoryImpl
import com.stevedenheyer.scriptassistant.common.data.waveform.reposititories.WaveformRepositoryImpl
import com.stevedenheyer.scriptassistant.common.domain.repositories.AudioRepository
import com.stevedenheyer.scriptassistant.common.domain.repositories.ProjectRepository
import com.stevedenheyer.scriptassistant.common.domain.repositories.SentencesRepository
import com.stevedenheyer.scriptassistant.common.domain.repositories.WaveformRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class ActivityRetainedModule {

    @ActivityRetainedScoped
    @Binds
    abstract fun bindProjectRepository(repository: ProjectRepositoryImpl): ProjectRepository

    @ActivityRetainedScoped
    @Binds
    abstract fun bindAudioRepository(repository: AudioRepositoryImpl): AudioRepository

    @ActivityRetainedScoped
    @Binds
    abstract fun bindWaveformRepository(repository: WaveformRepositoryImpl): WaveformRepository

/*

    companion object {

        @Provides
        fun provideSentencesCollector() = SentencesCollector()

    }
*/

}