package com.pperotti.android.sparq.demoapp.ui.main

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pperotti.android.sparq.demoapp.R
import com.pperotti.android.sparq.demoapp.ui.common.ErrorContent
import com.pperotti.android.sparq.demoapp.ui.common.LoadingContent

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = hiltViewModel(),
    onItemSelected: (id: Int) -> Unit,
) {
    // Invoke fetchData when the screen is first displayed
    LaunchedEffect(true) {
        mainViewModel.requestData()
    }

    // Collect data from the ViewModel and react to it
    mainViewModel.uiState.collectAsState().value.let { state ->
        // Draw the content by the state
        DrawScreenContent(state, modifier, onItemSelected)
    }
}

@Composable
fun DrawScreenContent(
    uiState: MainUiState,
    modifier: Modifier,
    onItemSelected: (id: Int) -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { MainScreenTopAppBar(modifier) }
    ) { paddingValues ->
        when (uiState) {
            is MainUiState.Loading -> LoadingContent(modifier)
            is MainUiState.Success -> MainListContent(
                uiItems = uiState.items,
                modifier = modifier.padding(paddingValues),
                onItemSelected = onItemSelected
            )

            is MainUiState.Error -> ErrorContent(uiState.message, modifier)
        }
    }
}

@Composable
fun MainListContent(
    uiItems: List<MainListItemUiState>,
    modifier: Modifier,
    onItemSelected: (id: Int) -> Unit
) {
    val configuration = LocalConfiguration.current
    val columnSize = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 2 else 1

    // Display the appropriate content based on the UI state
    LazyVerticalGrid(
        columns = GridCells.Fixed(columnSize),
        contentPadding = PaddingValues(16.dp),
        modifier = modifier
    ) {
        items(uiItems) { item ->
            CardItemComposable(item, onItemSelected = onItemSelected)
            Spacer(modifier = Modifier.height(16.dp)) // Add space between cards
        }
    }
}

@Composable
fun CardItemComposable(
    item: MainListItemUiState,
    onItemSelected: (id: Int) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxSize(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = {
            onItemSelected(item.id)
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.title ?: "-",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.primary)
                    .fillMaxWidth()
                    .height(2.dp)
            )
            Text(
                text = item.description ?: "-",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenTopAppBar(modifier: Modifier) {
    TopAppBar(
        title = {
            Box(
                modifier = modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.main_list_top_bar_title),
                    modifier = modifier,
                    fontSize = 20.sp
                )
            }
        },
        colors = TopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            scrolledContainerColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        modifier = modifier.fillMaxWidth()
    )
}