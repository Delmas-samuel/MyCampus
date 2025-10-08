package com.example.mycampus.ui.screens.report

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mycampus.domain.model.MediaType
import com.example.mycampus.ui.viewmodel.ReportViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ReportScreen(
    viewModel: ReportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Permission state
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // Photo captured successfully
        }
    }

    // Video launcher
    val videoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo()
    ) { success ->
        if (success) {
            // Video captured successfully
        }
    }

    // Image picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.setMediaUri(it, MediaType.PHOTO)
        }
    }

    // Video picker
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.setMediaUri(it, MediaType.VIDEO)
        }
    }

    LaunchedEffect(Unit) {
        if (permissionsState.allPermissionsGranted) {
            viewModel.getCurrentLocation()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Signalement d'incident",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Permission request card
            if (!permissionsState.allPermissionsGranted) {
                PermissionRequestCard(
                    onRequestPermissions = { permissionsState.launchMultiplePermissionRequest() }
                )
            }

            // Title field
            OutlinedTextField(
                value = uiState.title,
                onValueChange = { viewModel.updateTitle(it) },
                label = { Text("Titre du signalement *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.titleError != null,
                supportingText = {
                    uiState.titleError?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Title,
                        contentDescription = "Titre"
                    )
                }
            )

            // Description field
            OutlinedTextField(
                value = uiState.description,
                onValueChange = { viewModel.updateDescription(it) },
                label = { Text("Description détaillée *") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                maxLines = 5,
                isError = uiState.descriptionError != null,
                supportingText = {
                    uiState.descriptionError?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = "Description"
                    )
                }
            )

            // Media section
            MediaSection(
                mediaUri = uiState.mediaUri,
                onCameraClick = {
                    if (permissionsState.allPermissionsGranted) {
                        // Launch camera
                        imagePickerLauncher.launch("image/*")
                    } else {
                        permissionsState.launchMultiplePermissionRequest()
                    }
                },
                onVideoClick = {
                    if (permissionsState.allPermissionsGranted) {
                        // Launch video
                        videoPickerLauncher.launch("video/*")
                    } else {
                        permissionsState.launchMultiplePermissionRequest()
                    }
                },
                onGalleryClick = {
                    imagePickerLauncher.launch("image/*")
                },
                onPreviewClick = {
                    viewModel.dismissMediaPreview()
                }
            )

            // Location section
            LocationSection(
                location = uiState.currentLocation,
                isLoading = uiState.isLoadingLocation,
                error = uiState.locationError,
                onRefreshLocation = {
                    if (permissionsState.allPermissionsGranted) {
                        viewModel.getCurrentLocation()
                    } else {
                        permissionsState.launchMultiplePermissionRequest()
                    }
                }
            )

            // General error
            uiState.generalError?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Erreur",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Submit button
            Button(
                onClick = { viewModel.submitReport() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !uiState.isSubmitting && permissionsState.allPermissionsGranted,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Envoi en cours...")
                } else {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Envoyer"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Soumettre le signalement",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Info card
            InfoCard()
        }
    }

    // Success dialog
    if (uiState.showSuccessDialog) {
        SuccessDialog(
            onDismiss = { viewModel.dismissSuccessDialog() }
        )
    }
}
