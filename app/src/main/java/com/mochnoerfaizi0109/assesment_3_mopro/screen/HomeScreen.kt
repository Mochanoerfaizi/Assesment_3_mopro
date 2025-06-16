package com.mochnoerfaizi0109.assesment_3_mopro.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mochnoerfaizi0109.assesment_3_mopro.R
import com.mochnoerfaizi0109.assesment_3_mopro.ui.theme.Assesment_3_moproTheme

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Selamat Datang di Aplikasi OOTD", // Changed text
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Button(onClick = { navController.navigate("main_screen") }) {
            Text(text = stringResource(id = R.string.lihat_daftar_buku)) // Changed string resource
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    Assesment_3_moproTheme {
        HomeScreen(rememberNavController())
    }
}