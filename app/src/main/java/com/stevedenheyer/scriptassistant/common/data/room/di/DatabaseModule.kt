package com.stevedenheyer.scriptassistant.common.data.room.di

import android.content.Context
import androidx.work.WorkManager
import com.stevedenheyer.scriptassistant.common.data.room.daos.AudioFileDao
import com.stevedenheyer.scriptassistant.common.data.room.daos.ProjectDao
import com.stevedenheyer.scriptassistant.common.data.room.ScriptAssistDatabase
import com.stevedenheyer.scriptassistant.common.data.room.daos.AudioDetailsDao
import com.stevedenheyer.scriptassistant.common.data.waveform.GetWaveform
import com.stevedenheyer.scriptassistant.common.data.waveform.WaveformGenerator
import com.stevedenheyer.scriptassistant.common.data.waveform.WaveformsCollector
import com.stevedenheyer.scriptassistant.di.ApplicationScope
import com.stevedenheyer.scriptassistant.di.IoDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideProjectDatabase(@ApplicationContext context: Context): ScriptAssistDatabase {
        return ScriptAssistDatabase.getInstance(context)
    }

    @Provides
    fun provideProjectDao(scriptAssistDatabase: ScriptAssistDatabase): ProjectDao {
        return scriptAssistDatabase.projectDao()
    }

    @Provides
    fun provideAudioFileDao(scriptAssistDatabase: ScriptAssistDatabase): AudioFileDao {
        return scriptAssistDatabase.audioFileDao()
    }

    @Provides
    fun provideAudioDetailsDao(scriptAssistDatabase: ScriptAssistDatabase): AudioDetailsDao {
        return scriptAssistDatabase.audioDetailsDao()
    }

    @Provides
    fun provideWaveformsCollector(@ApplicationScope scope: CoroutineScope,
                            @ApplicationContext context: Context,
                            @IoDispatcher ioDispatcher: CoroutineDispatcher) = WaveformsCollector(scope, context, ioDispatcher)


/*
    @Singleton
    @Provides
    fun provideWorkManager(@ApplicationContext context: Context) = WorkManager.getInstance(context)
*/

}