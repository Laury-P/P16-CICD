package com.openclassroom.eventorias.features.events.eventList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openclassroom.eventorias.R
import com.openclassroom.eventorias.core.ui.theme.EventoriasTheme
import com.openclassroom.eventorias.core.utils.toFormattedDateString
import com.openclassroom.eventorias.features.events.eventList.component.CustomSearchBar
import com.openclassroom.eventorias.features.events.eventList.component.ErrorScreen
import com.openclassroom.eventorias.features.events.eventList.component.EventItem
import com.openclassroom.eventorias.features.events.eventList.component.FilterScreen
import com.openclassroom.eventorias.features.events.eventList.component.LoadingComponent
import com.openclassroom.eventorias.features.events.eventList.model.ListEventUiState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.AddEventScreenDestination
import com.ramcosta.composedestinations.generated.destinations.EventDetailScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun EventListScreen(
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
    viewModel: EventListViewModel = hiltViewModel()
) {
    val dims = EventoriasTheme.dimensions

    val listEventState by viewModel.uiState.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()

    var isSearchBarActive by remember { mutableStateOf(false) } // For the search bar
    val focusRequester = remember { FocusRequester() }

    var isFilterExposed by remember { mutableStateOf(false) } // For the filter dropDownMenu

    LaunchedEffect(isSearchBarActive) {
        if (isSearchBarActive) focusRequester.requestFocus()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    // When in active search mode, a text field, and when no active search, the title of the screen/
                    if (isSearchBarActive) {
                        CustomSearchBar(
                            searchQuery = searchQuery,
                            onQueryChanged = { viewModel.setSearchQuery(it) },
                            focusRequester = focusRequester,
                        )
                    } else Text(stringResource(R.string.event_list_screen_title))
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground,
                ),
                // Icon only display in active search mode to close the search bar
                navigationIcon = {
                    if (isSearchBarActive) {
                        IconButton(onClick = {
                            viewModel.setSearchQuery("")
                            isSearchBarActive = false
                        }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.close_searchbar_description)
                            )
                        }
                    }
                },
                actions = {
                    // When the search isn't active the buttons are the search button and the filter button
                    if (!isSearchBarActive) {
                        IconButton(
                            modifier = Modifier.testTag("search_button"),
                            onClick = { isSearchBarActive = true },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.search_button_description)
                            )
                        }

                        IconButton(
                            modifier = Modifier.testTag("filter_button"),
                            onClick = { isFilterExposed = !isFilterExposed }
                        ) {
                            Icon(
                                imageVector = if (isFilterExposed) Icons.Default.Close else Icons.AutoMirrored.Filled.Sort,
                                contentDescription = stringResource(
                                    if (isFilterExposed) R.string.close_sort_description
                                    else R.string.sort_button_description
                                )
                            )
                        }
                    } else {
                        // When the search is active, the button delete the active query
                        IconButton(
                            modifier = Modifier.testTag("close_search_button"),
                            onClick = {
                                if (searchQuery.isNotEmpty()) viewModel.setSearchQuery("")
                                else isSearchBarActive = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = stringResource(R.string.delete_search_description)
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigator.navigate(AddEventScreenDestination) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_fab_description)
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = dims.padding24)
        ) {
            if (isFilterExposed) {
                FilterScreen(
                    modifier = Modifier.fillMaxWidth(),
                    selectedCategory = selectedCategory,
                    onChipsSelected = { viewModel.setCategoryFilter(it) },
                    selectedDate = selectedDate,
                    onDateSelected = { viewModel.setDateFilter(it) },
                )
            }
            when (val currentState = listEventState) {
                is ListEventUiState.Loading -> LoadingComponent()
                is ListEventUiState.Error -> ErrorScreen(onRetryClick = { viewModel.retry() })
                is ListEventUiState.Success -> {
                    if (!isFilterExposed) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(dims.padding8)
                        ) {
                            selectedDate?.let {
                                FilterChip(
                                    selected = true,
                                    onClick = { viewModel.setDateFilter(null) },
                                    label = { Text(it.toFormattedDateString()) },
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = stringResource(R.string.remove_date_filter_description),
                                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                                        )
                                    },
                                    modifier = Modifier.testTag("date_filter_chip")
                                )
                            }
                            selectedCategory?.let {
                                FilterChip(
                                    selected = true,
                                    label = { Text(stringResource(it.labelResId)) },
                                    onClick = { viewModel.setCategoryFilter(null) },
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = stringResource(R.string.remove_category_filter_description),
                                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                                        )
                                    },
                                    modifier = Modifier.testTag("category_filter_chip")
                                )
                            }
                        }
                    }
                    val list = currentState.listEvent
                    if (list.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.empty_list),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    } else {
                        LazyColumn {
                            items(list) { uiEvent ->
                                val eventID = uiEvent.event.id
                                EventItem(uiEvent = uiEvent, onEventClick = { navigator.navigate(
                                    EventDetailScreenDestination(eventId = eventID)
                                ) })
                            }
                        }
                    }
                }
            }
        }

    }
}


