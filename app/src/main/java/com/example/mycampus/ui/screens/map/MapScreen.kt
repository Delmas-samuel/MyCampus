package com.example.mycampus.ui.screens.map

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.mycampus.data.repository.GeocodeSuggestion
import com.example.mycampus.ui.viewmodel.MapViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import com.example.mycampus.R



@Composable
fun MapScreen(viewModel: MapViewModel) {
    val dataState by viewModel.dataState.collectAsState()
    var hasPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasPermission = granted }
    )

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            viewModel.loadUserLocation()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasPermission) {
            when {
                dataState.userLocation != null -> {
                    MapContentWithSearch(dataState, viewModel)
                }
                dataState.locationError -> {
                    LocationErrorScreen { viewModel.loadUserLocation() }
                }
                else -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        } else {
            PermissionRequestScreen {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }
}

@Composable
private fun MapContentWithSearch(
    dataState: MapViewModel.MapDataState,
    viewModel: MapViewModel
) {
    val context = LocalContext.current

    Configuration.getInstance().userAgentValue = context.packageName
    Configuration.getInstance().load(
        context,
        context.getSharedPreferences("osmdroid", 0)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)

                    dataState.userLocation?.let { user ->
                        val userPoint = GeoPoint(user.latitude, user.longitude)
                        controller.setZoom(16.5)
                        controller.setCenter(userPoint)
                        overlays.add(Marker(this).apply {
                            position = userPoint
                            title = "Ma position actuelle"
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            icon = ctx.getDrawable(org.osmdroid.library.R.drawable.person)
                        })
                    }

                    val es = dataState.esatic.position
                    overlays.add(Marker(this).apply {
                        position = GeoPoint(es.latitude, es.longitude)
                        title = dataState.esatic.title
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    })

                    val lib = dataState.library.position
                    overlays.add(Marker(this).apply {
                        position = GeoPoint(lib.latitude, lib.longitude)
                        title = dataState.library.title
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    })

                    dataState.selectedDestination?.let { dest ->
                        overlays.add(Marker(this).apply {
                            position = GeoPoint(dest.position.latitude, dest.position.longitude)
                            title = dest.title
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        })
                    }

                    val routeUi: RouteUi? = dataState.routeToDestination
                    val geoPoints: List<GeoPoint>? = routeUi?.geoPoints
                    if (geoPoints != null && geoPoints.isNotEmpty()) {
                        val polyline = Polyline().apply {
                            outlinePaint.color = android.graphics.Color.BLUE
                            outlinePaint.strokeWidth = 8f
                            setPoints(geoPoints)
                        }
                        overlays.add(polyline)

                        val bbox = org.osmdroid.util.BoundingBox.fromGeoPointsSafe(polyline.actualPoints)
                        zoomToBoundingBox(bbox, true)
                    }
                }
            }
        )

        MapSearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(),
            query = dataState.query,
            suggestions = dataState.suggestions,
            searching = dataState.searching,
            onQueryChange = viewModel::onQueryChange,
            onSearch = viewModel::onSearchSubmit,
            onSuggestionClick = viewModel::onSuggestionClick
        )


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MapSearchBar(
    modifier: Modifier = Modifier,
    query: String,
    suggestions: List<GeocodeSuggestion>,
    searching: Boolean,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onSuggestionClick: (GeocodeSuggestion) -> Unit
) {
    var active by remember { mutableStateOf(false) }

    Column(modifier = modifier.padding(12.dp)) {
        SearchBar(
            query = query,
            onQueryChange = {
                onQueryChange(it)
                active = it.isNotBlank()
            },
            onSearch = {
                onSearch()
                active = false
            },
            active = active,
            onActiveChange = { active = it },
            placeholder = { Text("Rechercher un lieu") },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (searching) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            suggestions.forEach { s ->
                ListItem(
                    headlineContent = { Text(s.title) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSuggestionClick(s)
                            active = false
                        }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun PermissionRequestScreen(onRequest: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Permission de localisation requise", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRequest) {
                Text("Autoriser")
            }
        }
    }
}

@Composable
private fun LocationErrorScreen(onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Impossible d'obtenir la localisation", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Réessayer")
            }
        }
    }
}
