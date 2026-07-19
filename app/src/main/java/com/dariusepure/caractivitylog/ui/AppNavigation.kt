package com.dariusepure.caractivitylog.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dariusepure.caractivitylog.ui.auth.SignInScreen
import com.dariusepure.caractivitylog.ui.auth.SignUpScreen
import com.dariusepure.caractivitylog.ui.cars.AddCarScreen
import com.dariusepure.caractivitylog.ui.cars.CarDetailsScreen
import com.dariusepure.caractivitylog.ui.cars.CarListScreen
import com.dariusepure.caractivitylog.ui.cars.MileageHistoryScreen

import androidx.hilt.navigation.compose.hiltViewModel
import com.dariusepure.caractivitylog.ui.theme.ThemeViewModel

sealed class Screen(val route: String) {
    data object SignIn : Screen("signin")
    data object SignUp : Screen("signup")
    data object CarList : Screen("carlist")
    data object CarDetails : Screen("cardetails/{carId}") {
        fun createRoute(carId: String) = "cardetails/$carId"
    }
    data object MileageHistory : Screen("mileagehistory/{carId}") {
        fun createRoute(carId: String) = "mileagehistory/$carId"
    }
    data object AddCar : Screen("addcar")
    data object EditCar : Screen("editcar/{carId}") {
        fun createRoute(carId: String) = "editcar/$carId"
    }
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.CarList.route,
    themeViewModel: ThemeViewModel = hiltViewModel()
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.SignIn.route) {
            SignInScreen(
                onSignedIn = {
                    navController.navigate(Screen.CarList.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                },
                onSignUpClick = {
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignedIn = {
                    navController.navigate(Screen.CarList.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                },
                onBackToSignIn = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.CarList.route) {
            CarListScreen(
                onCarClick = { carId ->
                    navController.navigate(Screen.CarDetails.createRoute(carId))
                },
                onAddCarClick = {
                    navController.navigate(Screen.AddCar.route)
                },
                onEditCarClick = { carId ->
                    navController.navigate(Screen.EditCar.createRoute(carId))
                },
                onLogout = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                },
                themeViewModel = themeViewModel
            )
        }
        composable(Screen.CarDetails.route) { backStackEntry ->
            val carId = backStackEntry.arguments?.getString("carId") ?: return@composable
            CarDetailsScreen(
                carId = carId,
                onBack = { navController.popBackStack() },
                onMileageClick = {
                    navController.navigate(Screen.MileageHistory.createRoute(carId))
                }
            )
        }
        composable(Screen.MileageHistory.route) { backStackEntry ->
            val carId = backStackEntry.arguments?.getString("carId") ?: return@composable
            MileageHistoryScreen(
                carId = carId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AddCar.route) {
            AddCarScreen(
                onCarSaved = {
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.EditCar.route) { backStackEntry ->
            val carId = backStackEntry.arguments?.getString("carId") ?: return@composable
            AddCarScreen(
                carId = carId,
                onCarSaved = {
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
