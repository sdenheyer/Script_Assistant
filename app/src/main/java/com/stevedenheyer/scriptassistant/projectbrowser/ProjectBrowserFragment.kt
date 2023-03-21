package com.stevedenheyer.scriptassistant.projectbrowser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.fragment.findNavController
import com.stevedenheyer.scriptassistant.common.domain.model.project.Project
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.ui.graphics.Color
import androidx.core.os.bundleOf
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navGraphViewModels
import com.stevedenheyer.scriptassistant.R
import com.stevedenheyer.scriptassistant.audioeditor.viewmodels.WaveformRecyclerViewModel
import com.stevedenheyer.scriptassistant.common.components.ChooseNameDialog
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProjectBrowserFragment : Fragment() {

    companion object {
        fun newInstance() = ProjectBrowserFragment()
    }

    private val viewModel: ProjectBrowserViewModel by navGraphViewModels(R.id.introFragment) {defaultViewModelProviderFactory}


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        lifecycleScope.launch {
            viewModel.requestOpen.collect { event ->
                if (event is ProjectBrowserEvent.requestOpenProject) {
                    val action = ProjectBrowserFragmentDirections.actionIntroFragmentToAudioEditorFragment(event.projectId)
                    findNavController().navigate(action)
                }
            }
        }

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ProjectBrowser(viewModel)
            }
        }
    }

    @OptIn(ExperimentalLifecycleComposeApi::class)
    @Composable
    fun ProjectBrowser(viewModel:ProjectBrowserViewModel) {
        val projectList:List<Project> by viewModel.projectList.collectAsStateWithLifecycle()
        val selectedProject:Long by viewModel.selectedProject.observeAsState(-1)
        val createDialogOpen = remember { mutableStateOf(false) }

        Column(verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()) {
            ProjectList(modifier = Modifier.fillMaxWidth(), projectList, selectedProject, setSelectedProject = viewModel::setSelectedProject)
            ButtonRow(modifier = Modifier
                .fillMaxWidth(), viewModel::openProject, createDialogOpen, viewModel::deleteProject
           )
        }

        ChooseNameDialog(modifier = Modifier, createDialogOpen = createDialogOpen, viewModel::createNewProject)
    }

    @Composable
    fun ProjectList(modifier: Modifier, projectList: List<Project>, selectedProject: Long, setSelectedProject: (Long) -> Unit) {
        LazyColumn(modifier = modifier) {
            items(items = projectList, key = { project -> project.id}) { project ->
                Text(modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (project.id == selectedProject) Color.Blue else Color.Transparent
                    )
                    .selectable(selected = project.id == selectedProject,
                        onClick = { setSelectedProject(project.id) })
                    .padding(vertical = 8.dp),
                    text = project.name, fontSize = 20.sp
                        )
            }
        }
    }

    @Composable
    fun ButtonRow (modifier: Modifier, openProject: () -> Unit, createDialogOpen: MutableState<Boolean>, deleteProject: () -> Unit) {
        Row(horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = modifier) {
            NewProjectButton(modifier = Modifier, createDialogOpen)
            OpenProjectButton(modifier = Modifier, openProject)
            DeleteProjectButton(modifier = Modifier, deleteProject)
        }
    }

    @Composable
    fun NewProjectButton (modifier: Modifier, createDialogOpen: MutableState<Boolean>) {
        Button(modifier = modifier, onClick = { createDialogOpen.value = true }) {
            Text(text = "New Project")
        }
    }

    @Composable
    fun OpenProjectButton (modifier: Modifier, openProject: () -> Unit) {
        Button(modifier = modifier, onClick = {
            openProject()
        }) {
            Text(text = "Open Project")
        }
    }

    @Composable
    fun DeleteProjectButton (modifier: Modifier, deleteProject: () -> Unit) {
        Button(modifier = modifier, onClick = { deleteProject() }) {
            Text(text = "Delete Project")
        }
    }

}