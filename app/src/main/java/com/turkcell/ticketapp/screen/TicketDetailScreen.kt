package com.turkcell.ticketapp.screen

import android.app.Activity
import android.graphics.Bitmap
import android.view.WindowManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.turkcell.core.domain.purchase.TicketStatus
import com.turkcell.ticketapp.R
import com.turkcell.ticketapp.viewmodel.TicketDetailViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailScreen(
    ticketId: String,
    viewModel: TicketDetailViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val activity = context as? Activity
        val originalBrightness = activity?.window?.attributes?.screenBrightness
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        activity?.window?.attributes?.screenBrightness = 1f
        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            originalBrightness?.let {
                activity?.window?.attributes?.screenBrightness = it
            }
        }
    }

    LaunchedEffect(ticketId) {
        viewModel.loadTicket(ticketId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.ticket_detail_title)) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text(stringResource(R.string.back_button))
                    }
                }
            )
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
            state.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.error ?: stringResource(R.string.error_title),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            state.ticket != null -> {
                val ticket = state.ticket!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val qrBitmap = remember(ticket.qrCode) {
                        try {
                            val encoder = BarcodeEncoder()
                            val bitmap: Bitmap = encoder.encodeBitmap(
                                ticket.qrCode,
                                BarcodeFormat.QR_CODE,
                                800, 800
                            )
                            bitmap.asImageBitmap()
                        } catch (e: Exception) {
                            null
                        }
                    }

                    if (qrBitmap != null) {
                        Image(
                            bitmap = qrBitmap,
                            contentDescription = "QR Kodu",
                            modifier = Modifier.size(280.dp)
                        )
                    } else {
                        Text(stringResource(R.string.qr_failed))
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = ticket.eventName,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = ticket.ticketTypeName,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = stringResource(
                            R.string.ticket_status_label,
                            if (ticket.status == TicketStatus.VALID)
                                stringResource(R.string.ticket_valid)
                            else
                                stringResource(R.string.ticket_used)
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}