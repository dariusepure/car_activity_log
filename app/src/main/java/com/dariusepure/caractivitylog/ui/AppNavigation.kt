package com.dariusepure.caractivitylog.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dariusepure.caractivitylog.ui.auth.SignInScreen
import com.dariusepure.caractivitylog.ui.auth.SignUpScreen
import com.dariusepure.caractivitylog.ui.cars.AddCarScreen
import com.dariusepure.caractivitylog.ui.cars.CarListScreen

sealed class Screen(val route: String) {
    data object SignIn : Screen("signin")
    data object SignUp : Screen("signup")
    data object CarList : Screen("carlist")
    data object AddCar : Screen("addcar")
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.SignIn.route
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
                    // Navigate to car activities when implemented
                },
                onAddCarClick = {
                    navController.navigate(Screen.AddCar.route)
                }
            )
        }
        composable(Screen.AddCar.route) {
            AddCarScreen(
                onCarAdded = {
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
