package com.stevedenheyer.scriptassistant.projectbrowser

sealed class ProjectBrowserEvent {
    class requestOpenProject(val projectId: Long): ProjectBrowserEvent()
}