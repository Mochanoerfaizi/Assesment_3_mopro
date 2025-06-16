package com.mochnoerfaizi0109.assesment_3_mopro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mochnoerfaizi0109.assesment_3_mopro.screen.*
import com.mochnoerfaizi0109.assesment_3_mopro.ui.theme.Assesment_3_moproTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Assesment_3_moproTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val mainViewModel: MainViewModel = viewModel()

                    NavHost(
                        navController = navController,
                        // DIUBAH: Tujuan awal sekarang adalah home_screen
                        startDestination = "home_screen"
                    ) {
                        // DIHAPUS: Composable untuk splash screen tidak diperlukan lagi
                        // composable("splash_screen") { SplashScreen(navController) }

                        composable("home_screen") { HomeScreen(navController) }

                        composable("main_screen") {
                            MainScreen(navController = navController, viewModel = mainViewModel)
                        }

                        composable(
                            route = "detail_screen/{bukuId}",
                            arguments = listOf(navArgument("bukuId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val bukuId = backStackEntry.arguments?.getInt("bukuId")
                            val buku = bukuId?.let { mainViewModel.getBukuById(it) }

                            if (buku != null) {
                                DetailScreen(navController = navController, buku = buku)
                            } else {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("Buku tidak ditemukan.")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}