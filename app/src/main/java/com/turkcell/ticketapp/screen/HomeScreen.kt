package com.turkcell.ticketapp.screen

import android.os.Build
import android.text.format.DateUtils.formatDateTime
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.turkcell.ticketapp.viewmodel.EventsUiState
import com.turkcell.ticketapp.viewmodel.EventsViewModel
import com.turkcell.ticketapp.viewmodel.MyTicketsUiState
import com.turkcell.ticketapp.viewmodel.MyTicketsViewModel
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    eventsViewModel: EventsViewModel = koinViewModel(),
    ticketsViewModel: MyTicketsViewModel = koinViewModel()
)
{
    val eventsState by eventsViewModel.state.collectAsStateWithLifecycle()
    val ticketsState by ticketsViewModel.state.collectAsStateWithLifecycle()

    // Sekmeler için index hatırlayıcı
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Scaffold(modifier = Modifier.statusBarsPadding(),
        topBar = {
            TabRow(selectedTabIndex = selectedTabIndex) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Etkinlikler") }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Biletlerim") }
                )
            }
        }
    ) { paddingValues ->
        when (selectedTabIndex) {
            0 -> EventsList(
                state = eventsState,
                modifier = Modifier.padding(paddingValues)
            )
            1 -> MyTicketsList(
                state = ticketsState,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun EventsList(state: EventsUiState, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (state.errorMessage != null) {
            Text(
                text = state.errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn {
                items(state.events) { event ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = event.name, style = MaterialTheme.typography.titleMedium)
                            Text(text = event.venue, style = MaterialTheme.typography.bodyMedium)
                            Text(text = formatDateTime(event.startsAt)) // DateFormatter kullanılabilir
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MyTicketsList(state: MyTicketsUiState, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (state.errorMessage != null) {
            Text(
                text = state.errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn {
                items(state.tickets) { ticket ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = ticket.event.name, style = MaterialTheme.typography.titleMedium)
                            Text(text = "Bilet: ${ticket.ticketType.name}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Durum: ${ticket.status}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

// TODO: daha sonra bunun yerine  projedeki dateformatter kullancam.
@RequiresApi(Build.VERSION_CODES.O)
fun formatDateTime(isoString: String): String {
    return try {
        val instant = java.time.Instant.parse(isoString)
        val localDateTime = java.time.LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault())
        val formatter = java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")
        localDateTime.format(formatter)
    } catch (e: Exception) {
        isoString
    }
}