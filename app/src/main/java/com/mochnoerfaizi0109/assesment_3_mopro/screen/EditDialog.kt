package com.mochnoerfaizi0109.assesment_3_mopro.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.mochnoerfaizi0109.assesment_3_mopro.R
import com.mochnoerfaizi0109.assesment_3_mopro.model.Buku

@Composable
fun EditDialog(
    bukuToEdit: Buku,
    onDismissRequest: () -> Unit,
    onConfirmation: (String, String) -> Unit
) {

    var namaBuku by remember { mutableStateOf(bukuToEdit.namaBuku ?: "") }
    var penulisBuku by remember { mutableStateOf(bukuToEdit.penulisBuku ?: "") }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.edit_buku),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = namaBuku,
                    onValueChange = { namaBuku = it },
                    label = { Text(text = stringResource(id = R.string.nama_buku)) },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = penulisBuku,
                    onValueChange = { penulisBuku = it },
                    label = { Text(text = stringResource(id = R.string.penulis)) },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = stringResource(R.string.batal))
                    }
                    OutlinedButton(

                        onClick = { onConfirmation(namaBuku, penulisBuku) },
                        enabled = namaBuku.isNotEmpty() && penulisBuku.isNotEmpty(),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = stringResource(R.string.simpan))
                    }
                }
            }
        }
    }
}