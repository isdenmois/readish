package com.isdenmois.readish.apps.presentational

import com.isdenmois.readish.apps.model.AppEntry
import com.isdenmois.readish.ui.LoadState

data class AppsUiState(val apps: LoadState<List<AppEntry>> = LoadState.Loading, val page: Int = 0)
