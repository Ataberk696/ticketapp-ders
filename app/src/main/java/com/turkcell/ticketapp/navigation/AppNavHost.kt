package com.turkcell.ticketapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.turkcell.core.domain.auth.AuthRepository
import com.turkcell.core.domain.auth.UserRole
import com.turkcell.ticketapp.screen.EventDetailScreen
import com.turkcell.ticketapp.screen.HomeScreen
import com.turkcell.ticketapp.screen.LoginScreen
import com.turkcell.ticketapp.screen.MyTicketsScreen
import com.turkcell.ticketapp.screen.RegisterScreen
import com.turkcell.ticketapp.screen.StaffScreen
import com.turkcell.ticketapp.screen.TicketDetailScreen
import org.koin.compose.koinInject


@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    authRepository: AuthRepository = koinInject()
)
{
    val isLoggedIn by authRepository.isLoggedIn.collectAsStateWithLifecycle(initialValue = false)
    val currentUser by authRepository.currentUser.collectAsStateWithLifecycle(initialValue = null)

    LaunchedEffect(isLoggedIn, currentUser) {
        if (isLoggedIn && currentUser == null) {
            authRepository.logout()
        }
    }

    when {
        !isLoggedIn -> UnAuthedNavHost(navController)
        currentUser == null -> SplashScreen()
        else -> {
            when (currentUser!!.role) {
                UserRole.USER -> AuthedNavHost(navController)
                UserRole.STAFF -> StaffNavHost(navController)
                UserRole.ADMIN -> AdminNavHost(navController)
            }
        }
    }

}


@Composable
private fun SplashScreen(){
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        CircularProgressIndicator()
    }
}

@Composable
private fun AuthedNavHost(navController: NavHostController){
    NavHost(navController=navController, startDestination = Home){
        composable<Home> {
            HomeScreen(
                onEventClick = {eventId ->
                    navController.navigate(EventDetail(eventId))
                },
                onNavigateToTickets = {navController.navigate(MyTickets)}
            )
        }
        composable<EventDetail> { backStackEntry ->
            val route: EventDetail = backStackEntry.toRoute()
            EventDetailScreen(
                eventId = route.id,
                onBack = { navController.popBackStack() },
                onPurchaseSuccess = {
                    navController.navigate(MyTickets) {
                        popUpTo(Home) { inclusive = false }
                    }
                }
            )
        }
        composable<MyTickets> {
            MyTicketsScreen(
                onBack = { navController.popBackStack() },
                onTicketClick = { ticketId ->
                    navController.navigate(TicketDetail(ticketId))
                }
            )
        }
        composable<TicketDetail> { backStackEntry ->
            val route: TicketDetail = backStackEntry.toRoute()
            TicketDetailScreen(
                ticketId = route.ticketId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun UnAuthedNavHost(navController: NavHostController){
    NavHost(navController=navController, startDestination = Login) {
        composable<Login>{
            LoginScreen(
                onLoginSuccess = {},
                onNavigateToRegister = {navController.navigate(Register)}
            )
        }
        composable<Register> {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Home) {
                        popUpTo(Login) { inclusive = true }
                    }
                },
                onNavigateToLogin = {navController.navigate(Login)}
            )
        }
    }
}
@Composable
private fun StaffNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Staff) {
        composable<Staff> {
            StaffScreen()
        }
    }
}

@Composable
private fun AdminNavHost(navController: NavHostController) {
    // İleride admin ekranlarını olursa buraya eklicem.
    NavHost(navController = navController, startDestination = Home) {
        composable<Home> {
            Text("Admin Paneli")
        }
    }
}