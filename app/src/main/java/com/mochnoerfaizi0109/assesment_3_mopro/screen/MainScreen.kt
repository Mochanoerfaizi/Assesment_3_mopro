package com.mochnoerfaizi0109.assesment_3_mopro.screen

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.mochnoerfaizi0109.assesment_3_mopro.BuildConfig
import com.mochnoerfaizi0109.assesment_3_mopro.R
import com.mochnoerfaizi0109.assesment_3_mopro.model.Buku
import com.mochnoerfaizi0109.assesment_3_mopro.network.UserDataStore
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.mochnoerfaizi0109.mobpro1.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, viewModel: MainViewModel) {
    val context = LocalContext.current
    val dataStore = UserDataStore(context)
    val scope = rememberCoroutineScope()
    val userSession by dataStore.userSessionFlow.collectAsState(initial = null)
    val errorMessage by viewModel.errorMessage
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    var showBukuDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showHapusDialog by remember { mutableStateOf(false) }
    var showProfilDialog by remember { mutableStateOf(false) }
    var showLoginPromptDialog by remember { mutableStateOf(false) }
    var bukuToOperate by remember { mutableStateOf<Buku?>(null) }

    val imageCropper = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            bitmap = getCroppedImage(context.contentResolver, result)
            showBukuDialog = true
        } else {
            Log.e("IMAGE_CROP", "Error: ${result.error?.message}")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) },
                actions = {
                    IconButton(onClick = {
                        if (userSession?.token.isNullOrEmpty()) {
                            scope.launch { signIn(context, viewModel, dataStore) }
                        } else {
                            showProfilDialog = true
                        }
                    }) { Icon(Icons.Default.Person, contentDescription = stringResource(R.string.profil)) }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (userSession?.token.isNullOrEmpty()) {
                    showLoginPromptDialog = true
                } else {
                    val cropOptions = CropImageOptions(imageSourceIncludeGallery = true, imageSourceIncludeCamera = true, fixAspectRatio = true)
                    imageCropper.launch(CropImageContractOptions(null, cropOptions))
                }
            }) { Icon(Icons.Default.Add, contentDescription = stringResource(R.string.tambah_buku)) }
        }
    ) { padding ->
        ScreenContent(
            viewModel = viewModel,
            currentUserId = userSession?.userId,
            modifier = Modifier.padding(padding),
            onDeleteRequest = { buku ->
                bukuToOperate = buku
                showHapusDialog = true
            },
            onEditRequest = { buku ->
                bukuToOperate = buku
                showEditDialog = true
            },
            onItemClick = { buku -> navController.navigate("detail_screen/${buku.id}") }
        )

        if (showBukuDialog) {
            BukuDialog(
                bitmap = bitmap,
                onDismissRequest = { showBukuDialog = false },
                onConfirmation = { nama, penulis ->
                    userSession?.token?.let { token ->
                        bitmap?.let { btm ->
                            viewModel.saveData(token, nama, penulis, btm)
                        }
                    }
                    showBukuDialog = false
                }
            )
        }
        if (showEditDialog && bukuToOperate != null) {
            EditDialog(
                bukuToEdit = bukuToOperate!!,
                onDismissRequest = { showEditDialog = false },
                onConfirmation = { newName, newPenulis ->
                    userSession?.token?.let { token ->
                        viewModel.updateData(token, bukuToOperate!!, newName, newPenulis, null)
                    }
                    showEditDialog = false
                }
            )
        }
        if (showHapusDialog && bukuToOperate != null) {
            HapusDialog(
                onDismissRequest = { showHapusDialog = false },
                onConfirmation = {
                    userSession?.token?.let { token -> viewModel.deleteBuku(token, bukuToOperate!!.id) }
                    showHapusDialog = false
                }
            )
        }
        if (showProfilDialog && userSession != null) {
            ProfilDialog(
                user = User(userSession!!.name ?: "", userSession!!.email ?: "", userSession!!.photoUrl ?: ""),
                onDismissRequest = { showProfilDialog = false },
                onConfirmation = {
                    scope.launch { dataStore.clearSession() }
                    showProfilDialog = false
                }
            )
        }
        if (showLoginPromptDialog) {
            AlertDialog(
                onDismissRequest = { showLoginPromptDialog = false },
                title = { Text("Login Diperlukan") },
                text = { Text("Anda harus login untuk bisa menambahkan data baru. Lanjutkan login dengan Google?") },
                confirmButton = {
                    Button(onClick = {
                        scope.launch { signIn(context, viewModel, dataStore) }
                        showLoginPromptDialog = false
                    }) { Text("Login") }
                },
                dismissButton = {
                    TextButton(onClick = { showLoginPromptDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }
        if (errorMessage != null) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            viewModel.clearMessage()
        }
    }
}

@Composable
fun ScreenContent(
    viewModel: MainViewModel,
    currentUserId: Int?,
    modifier: Modifier,
    onDeleteRequest: (Buku) -> Unit,
    onEditRequest: (Buku) -> Unit,
    onItemClick: (Buku) -> Unit
) {
    val allData by viewModel.data
    val status by viewModel.status.collectAsState()

    val displayedData = remember(allData, currentUserId) {
        val filteredList = if (currentUserId == null) {
            allData.filter { it.userId == null }
        } else {
            allData.filter { it.userId == null || it.userId == currentUserId }
        }
        filteredList.sortedBy { it.id }
    }

    LaunchedEffect(currentUserId) {
        viewModel.retrieveData()
    }

    when (status) {
        ApiStatus.LOADING -> Box(modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
        ApiStatus.SUCCESS -> LazyVerticalGrid(
            modifier = modifier
                .fillMaxSize()
                .padding(4.dp),
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(displayedData) { buku ->
                val canOperate = buku.userId != null && buku.userId == currentUserId
                ListItem(buku, canOperate, onDeleteRequest, onEditRequest, onItemClick)
            }
        }
        ApiStatus.FAILED -> Box(modifier.fillMaxSize(), Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.error))
                Button(onClick = { viewModel.retrieveData() }) { Text(stringResource(R.string.try_again)) }
            }
        }
    }
}


@Composable
fun ListItem(
    buku: Buku,
    canOperate: Boolean,
    onDeleteRequest: (Buku) -> Unit,
    onEditRequest: (Buku) -> Unit,
    onItemClick: (Buku) -> Unit
) {
    Card(modifier = Modifier
        .padding(4.dp)
        .clickable { onItemClick(buku) }) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(buku.gambar)
                    .crossfade(true)
                    .build(),

                contentDescription = buku.namaBuku ?: stringResource(id = R.string.gambar_buku),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.loading_img),
                error = painterResource(id = R.drawable.baseline_broken_image_24),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {

                    Text(
                        text = buku.namaBuku ?: stringResource(id = R.string.tanpa_judul),
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = buku.penulisBuku ?: stringResource(id = R.string.tanpa_penulis),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (canOperate) {
                    IconButton(onClick = { onEditRequest(buku) }) { Icon(Icons.Default.Edit, contentDescription = "Edit Teks") }
                    IconButton(onClick = { onDeleteRequest(buku) }) { Icon(Icons.Default.Delete, contentDescription = "Hapus") }
                }
            }
        }
    }
}

private suspend fun signIn(context: Context, viewModel: MainViewModel, dataStore: UserDataStore) {
    try {
        val credentialManager = CredentialManager.create(context)
        val result = credentialManager.getCredential(
            context,
            GetCredentialRequest.Builder().addCredentialOption(
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(BuildConfig.API_KEY)
                    .build()
            ).build()
        )
        handleSignIn(result, viewModel, dataStore)
    } catch (e: GetCredentialException) {
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
}

private fun handleSignIn(result: GetCredentialResponse, viewModel: MainViewModel, dataStore: UserDataStore) {
    try {
        val credential = result.credential as CustomCredential
        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdCredential = GoogleIdTokenCredential.createFrom(credential.data)
            viewModel.loginWithGoogle(googleIdCredential.idToken) { authResponse ->
                CoroutineScope(Dispatchers.Main).launch {
                    dataStore.saveSession(
                        token = authResponse.accessToken,
                        userId = authResponse.id,
                        name = googleIdCredential.displayName ?: "No Name",
                        email = googleIdCredential.id,
                        photoUrl = googleIdCredential.profilePictureUri.toString()
                    )
                }
            }
        }
    } catch (e: GoogleIdTokenParsingException) {
        Log.e("SIGN-IN", "Error parsing: ${e.message}")
    }
}

private fun getCroppedImage(resolver: ContentResolver, result: CropImageView.CropResult): Bitmap? {
    if (!result.isSuccessful) return null
    return result.uriContent?.let { uri ->
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(resolver, uri)
        } else {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(resolver, uri))
        }
    }
}