package com.stevedenheyer.scriptassistant.projectbrowser

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stevedenheyer.scriptassistant.common.components.ChooseNameDialog
import com.stevedenheyer.scriptassistant.common.domain.model.project.Project

@Composable
fun ProjectBrowserScreen(viewModel: ProjectBrowserViewModel, navigateToAudioEditor: (id: Long) -> Unit) {
    val projectList:List<Project> by viewModel.projectList.collectAsStateWithLifecycle()
    val selectedProject:Long by viewModel.selectedProject.observeAsState(-1)

    val createDialogOpen = remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()) {
        ProjectList(modifier = Modifier.fillMaxWidth(), projectList, selectedProject, setSelectedProject = viewModel::setSelectedProject)
        ButtonRow(modifier = Modifier
            .fillMaxWidth(), selectedProject, navigateToAudioEditor, createDialogOpen, viewModel::deleteProject
        )
    }

    ChooseNameDialog(modifier = Modifier, createDialogOpen = createDialogOpen, viewModel::createNewProject, navigateToAudioEditor)
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
fun ButtonRow (modifier: Modifier, selectedProject: Long, openProject: (id: Long) -> Unit, createDialogOpen: MutableState<Boolean>, deleteProject: () -> Unit) {
    Row(horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier) {
        NewProjectButton(modifier = Modifier, createDialogOpen)
        OpenProjectButton(modifier = Modifier, selectedProject, openProject)
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
fun OpenProjectButton (modifier: Modifier, selectedProject: Long, openProject: (id:Long) -> Unit) {
    Button(modifier = modifier, onClick = {
        //openProject()
        if (selectedProject != null && selectedProject > -1) {
            openProject(selectedProject)
        }
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