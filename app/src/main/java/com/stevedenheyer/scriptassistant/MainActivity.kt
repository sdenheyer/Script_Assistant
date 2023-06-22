package com.stevedenheyer.scriptassistant

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.os.Environment
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.navigation
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.stevedenheyer.scriptassistant.audioeditor.presentation.AudioEditorScreen
import com.stevedenheyer.scriptassistant.audioeditor.viewmodels.ScriptViewmodel
import com.stevedenheyer.scriptassistant.audioeditor.viewmodels.WaveformRecyclerViewModel
import com.stevedenheyer.scriptassistant.audioeditor.viewmodels.WaveformUniverseViewModel
import com.stevedenheyer.scriptassistant.filebrowser.domain.usecases.CreateNewAudioData
import com.stevedenheyer.scriptassistant.filebrowser.presentation.FileBrowserScreen
import com.stevedenheyer.scriptassistant.projectbrowser.ProjectBrowserScreen
import com.stevedenheyer.scriptassistant.projectbrowser.ProjectBrowserViewModel
import com.stevedenheyer.scriptassistant.utils.sampleRate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var createNewAudioData: CreateNewAudioData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_main)

        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        sampleRate = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE).toInt()

        setContent {
            MainActivityNavHost(modifier = Modifier)
        }


    }

    @Composable
    fun MainActivityNavHost(
        modifier: Modifier,
        navController: NavHostController = rememberNavController(),
        startDestination: String = "projectBrowser"
    ) {
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = startDestination
        ) {
            composable("projectBrowser") {
                val viewModel = hiltViewModel<ProjectBrowserViewModel>()
                ProjectBrowserScreen(viewModel = viewModel, navigateToAudioEditor =  { id ->
                    val destination = "audioEditor/" + id.toString()
                    navController.navigate(destination) })
            }
                navigation(
                    startDestination = "audioEditorMain",
                    route = "audioEditor/{projectId}",
                    arguments = listOf(navArgument("projectId") { type = NavType.LongType})
                ) {

                    composable(
                        "audioEditorMain",
                    ) { backStackEntry ->
                        val projectId = backStackEntry.arguments?.getLong("projectId")
                        val waveformRecyclerVM = hiltViewModel<WaveformRecyclerViewModel>()
                        val waveformUniverseVM = hiltViewModel<WaveformUniverseViewModel>()
                        val scriptVM = hiltViewModel<ScriptViewmodel>()

                        AudioEditorScreen(waveformRecyclerVM = waveformRecyclerVM,
                            waveformUniverseVM = waveformUniverseVM,
                            scriptVM = scriptVM,
                            onNavigateToImport = {
                                val destination = "fileBrowser/" + projectId.toString()
                                navController.navigate(destination)
                            },
                            onNavigateToScriptEditor = { scriptId ->
                                val destination = "scriptEditor/" + projectId.toString()
                                navController.navigate(destination)
                            })


                    }

                    composable("fileBrowser/{projectId}", arguments = listOf(navArgument("projectId") { type = NavType.LongType})) { backStackEntry ->
                        val projectId = backStackEntry.arguments?.getLong("projectId")
                        val scope = rememberCoroutineScope()
                        FileBrowserScreen(startingDir = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS),
                        onFileSelected = { file ->
                            scope.launch {
                                createNewAudioData(projectId!!, file.absolutePath)
                                navController.navigateUp()
                            }
                        })

                    }
                }
        }
    }
}