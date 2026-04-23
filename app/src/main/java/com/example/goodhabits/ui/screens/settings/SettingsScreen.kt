package com.example.goodhabits.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.goodhabits.R
import com.example.goodhabits.domain.repository.AppLanguage
import com.example.goodhabits.domain.repository.AppTheme
import com.example.goodhabits.ui.theme.Purple
import com.example.goodhabits.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val theme by viewModel.theme.collectAsState()
    val language by viewModel.language.collectAsState()
    val notificationsEnabled by viewModel.globalNotificationsEnabled.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            SettingsSection(title = stringResource(R.string.general_section)) {
                SettingsItem(
                    icon = Icons.Default.RateReview,
                    title = stringResource(R.string.rate_app),
                    onClick = {
                        val packageName = context.packageName
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("market://details?id=$packageName")
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            val webIntent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                            }
                            context.startActivity(webIntent)
                        }
                    }
                )
                SettingsItem(
                    icon = Icons.Default.ErrorOutline,
                    title = stringResource(R.string.report_bug),
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:markiyan05@gmail.com")
                            putExtra(Intent.EXTRA_SUBJECT, "Bug Report - GoodHabits")
                        }
                        context.startActivity(Intent.createChooser(intent, "Send Email"))
                    }
                )
                SettingsItem(
                    icon = Icons.Default.ChatBubbleOutline,
                    title = stringResource(R.string.suggest_feature),
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:markiyan05@gmail.com")
                            putExtra(Intent.EXTRA_SUBJECT, "Feature Suggestion - GoodHabits")
                        }
                        context.startActivity(Intent.createChooser(intent, "Send Email"))
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsSection(title = stringResource(R.string.notifications_section)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.NotificationsNone,
                            contentDescription = null,
                            tint = Purple,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = stringResource(R.string.global_notifications_label),
                            fontSize = 16.sp
                        )
                    }
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { viewModel.setGlobalNotificationsEnabled(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Purple, checkedTrackColor = Purple.copy(alpha = 0.5f))
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsSection(title = stringResource(R.string.appearance_section)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = Purple)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(stringResource(R.string.theme_label), fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeOption(
                            title = stringResource(R.string.theme_light),
                            selected = theme == AppTheme.LIGHT,
                            onClick = { viewModel.setTheme(AppTheme.LIGHT) },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeOption(
                            title = stringResource(R.string.theme_dark),
                            selected = theme == AppTheme.DARK,
                            onClick = { viewModel.setTheme(AppTheme.DARK) },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeOption(
                            title = stringResource(R.string.theme_system),
                            selected = theme == AppTheme.SYSTEM,
                            onClick = { viewModel.setTheme(AppTheme.SYSTEM) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = Purple)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(stringResource(R.string.language_label), fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeOption(
                            title = stringResource(R.string.language_uk),
                            selected = language == AppLanguage.UK,
                            onClick = { viewModel.setLanguage(AppLanguage.UK) },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeOption(
                            title = stringResource(R.string.language_en),
                            selected = language == AppLanguage.EN,
                            onClick = { viewModel.setLanguage(AppLanguage.EN) },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsSection(title = stringResource(R.string.data_section)) {
                SettingsItem(
                    icon = Icons.Default.DeleteSweep,
                    title = stringResource(R.string.clear_data_label),
                    textColor = Color.Red,
                    onClick = { showDeleteDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text(stringResource(R.string.clear_data_label)) },
                text = { Text(stringResource(R.string.clear_data_confirm_msg)) },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.clearAllData()
                        showDeleteDialog = false
                    }) {
                        Text(stringResource(R.string.clear), color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(content = content)
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    textColor: Color = Color.Unspecified,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon, 
            contentDescription = null, 
            tint = Purple, 
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title, 
            fontSize = 16.sp, 
            color = if (textColor == Color.Unspecified) MaterialTheme.colorScheme.onSurface else textColor
        )
    }
}

@Composable
fun ThemeOption(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (selected) Purple else Color(0xFFF5F5F5),
        contentColor = if (selected) Color.White else Color.Black,
        modifier = modifier.height(40.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(title, fontSize = 12.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
        }
    }
}
