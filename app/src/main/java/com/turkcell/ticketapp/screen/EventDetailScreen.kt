package com.turkcell.ticketapp.screen


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.turkcell.core.domain.event.TicketType
import com.turkcell.core.util.formatEventDate
import com.turkcell.ticketapp.viewmodel.EventDetailViewModel
import com.turkcell.ticketapp.viewmodel.PurchaseViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventId: String,
    viewModel: EventDetailViewModel = koinViewModel(),
    purchaseViewModel: PurchaseViewModel = koinViewModel(),
    onBack: () -> Unit,
    onPurchaseSuccess: () -> Unit = {}
)
{
    val state by viewModel.state.collectAsStateWithLifecycle()
    val purchaseState by purchaseViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    LaunchedEffect(purchaseState.isPaid) {
        if (purchaseState.isPaid) {
            onPurchaseSuccess()
        }
    }

    if (purchaseState.error !=null){
        AlertDialog(
            onDismissRequest = {purchaseViewModel.consumeError()},
            title = { Text("Hata") },
            text = { Text(purchaseState.error!!) },
            confirmButton = {
                TextButton(onClick = {purchaseViewModel.consumeError()}) {
                    Text("Tamam")
                }
            }
        )
    }

    if (purchaseState.showConfirmation && purchaseState.purchase != null){
        AlertDialog(
            onDismissRequest = {purchaseViewModel.dismissConfirmation()},
            title = { Text("Ödemeyi Onayla") },
            text = { Text("Toplam: ₺${purchaseState.purchase!!.totalCents / 100.0}\nÖdemeye devam etmek istiyor musunuz?") },
            confirmButton = {
                TextButton(onClick = { purchaseViewModel.confirmPayment() }) {
                    if (purchaseState.isPaying) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    } else {
                        Text("Öde")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { purchaseViewModel.dismissConfirmation() }) {
                    Text("Vazgeç")
                }
            }
        )
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text ("Etkinlik Detayı")},
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Geri") }
                }
            )
        },

        bottomBar = {
            if (state.event != null && state.canPurchase) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Toplam: ₺${state.totalCents / 100.0}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                val items = state.quantities
                                    .filter { it.value > 0 }
                                    .map { (ticketTypeId, quantity) ->
                                        Pair(ticketTypeId, quantity)
                                    }
                                purchaseViewModel.createPurchase(items)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = state.canPurchase && !purchaseState.isCreating
                        ) {
                            if (purchaseState.isCreating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = LocalContentColor.current
                                )
                            } else {
                                Text("Satın Al")
                            }
                        }
                    }
                }
            }
        }


    ) { paddingValues ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.error != null ->{
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues)
                ) {
                    Text(state.error!!, color = MaterialTheme.colorScheme.error)
                }
            }
            state.event != null -> {
                val event = state.event!!
                LazyColumn(
                    modifier = Modifier.padding(paddingValues),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        Text(
                            text = event.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = event.description,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Yer: ${event.venue}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tarih: ${formatEventDate(event.startsAt)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Bilet Türleri",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(event.ticketTypes, key = { it.id }) { ticketType ->
                        TicketTypeRow(
                            ticketType = ticketType,
                            quantity = state.quantities[ticketType.id] ?: 0,
                            onIncrease = { viewModel.onIncrease(ticketType.id) },
                            onDecrease = { viewModel.onDecrease(ticketType.id) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

    }
}

@Composable
private fun TicketTypeRow(
    ticketType: TicketType,
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
){
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ticketType.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "₺${ticketType.priceCents / 100.0} | Kalan: ${ticketType.remaining}/${ticketType.capacity}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDecrease, enabled = quantity > 0) {
                    Text("−", style = MaterialTheme.typography.titleMedium)
                }
                Text(
                    text = "$quantity",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.width(24.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                IconButton(
                    onClick = onIncrease,
                    enabled = quantity < minOf(20, ticketType.remaining.toInt())
                ) {
                    Text("+", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}