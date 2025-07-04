package com.mochnoerfaizi0109.assesment_3_mopro.screen

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mochnoerfaizi0109.assesment_3_mopro.R

@Composable
fun HapusDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {
    AlertDialog(
        text = {
            Text(text = "Yakin ingin menghapus data ini?")
        },
        confirmButton = {
            TextButton(onClick = onConfirmation) {
                Text(text = "Hapus")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Batal")
            }
        },
        onDismissRequest = onDismissRequest
    )
}