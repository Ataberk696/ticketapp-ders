package com.turkcell.ticketapp.di


import com.turkcell.ticketapp.viewmodel.EventDetailViewModel
import com.turkcell.ticketapp.viewmodel.HomeViewModel
import com.turkcell.ticketapp.viewmodel.LoginViewModel
import com.turkcell.ticketapp.viewmodel.MyTicketsViewModel
import com.turkcell.ticketapp.viewmodel.PurchaseViewModel
import com.turkcell.ticketapp.viewmodel.RegisterViewModel
import com.turkcell.ticketapp.viewmodel.TicketDetailViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    // viewmodel
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::EventDetailViewModel)
    viewModelOf(::PurchaseViewModel)
    viewModelOf(::MyTicketsViewModel)
    viewModelOf(::TicketDetailViewModel)
}