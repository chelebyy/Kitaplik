package com.example.deneme.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.deneme.data.model.Book
import com.example.deneme.data.model.ReadingStatus
import com.example.deneme.ui.viewmodel.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBookDialog(
    onDismiss: () -> Unit,
    viewModel: BookViewModel
) {
    var showManualDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var searchResults by remember { mutableStateOf<List<Book>>(emptyList()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Kitap Ekle",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                // Arama alanı
                TextField(
                    value = searchQuery,
                    onValueChange = { 
                        searchQuery = it
                        if (it.length >= 3) {
                            isSearching = true
                            viewModel.searchBooks(it) { books ->
                                searchResults = books
                                isSearching = false
                            }
                        } else {
                            searchResults = emptyList()
                        }
                    },
                    placeholder = { 
                        Text(
                            "Kitap adı veya yazar ara...",
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1
                        ) 
                    },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    singleLine = true,
                    shape = MaterialTheme.shapes.extraLarge,
                    trailingIcon = {
                        if (isSearching) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Ara",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = MaterialTheme.colorScheme.surface,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.surface,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )

                // Arama sonuçları
                if (searchResults.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        items(searchResults) { book ->
                            ListItem(
                                headlineContent = { Text(book.title) },
                                supportingContent = { Text(book.author) },
                                trailingContent = {
                                    IconButton(
                                        onClick = {
                                            viewModel.addBook(book)
                                            onDismiss()
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.Add,
                                            contentDescription = "Kitabı Ekle",
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Divider()
                        }
                    }
                }

                // Manuel ekleme butonu
                TextButton(
                    onClick = { showManualDialog = true },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Manuel Ekle", color = Color.White)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("İptal")
                    }
                }
            }
        }
    }

    if (showManualDialog) {
        ManualAddBookDialog(
            onDismiss = { showManualDialog = false },
            onBookAdded = { book ->
                viewModel.addBook(book)
                showManualDialog = false
                onDismiss()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualAddBookDialog(
    onDismiss: () -> Unit,
    onBookAdded: (Book) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var pageCount by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf(ReadingStatus.TO_READ) }
    var showStatusMenu by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Manuel Kitap Ekle",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Kitap Adı") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Yazar") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                OutlinedTextField(
                    value = pageCount,
                    onValueChange = { pageCount = it },
                    label = { Text("Sayfa Sayısı") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = when (selectedStatus) {
                            ReadingStatus.TO_READ -> "Okunacak"
                            ReadingStatus.READING -> "Okunuyor"
                            ReadingStatus.READ -> "Okundu"
                        },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Kategori") },
                        trailingIcon = {
                            IconButton(onClick = { showStatusMenu = true }) {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = "Kategori seç"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    DropdownMenu(
                        expanded = showStatusMenu,
                        onDismissRequest = { showStatusMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Okunacak") },
                            onClick = {
                                selectedStatus = ReadingStatus.TO_READ
                                showStatusMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Okunuyor") },
                            onClick = {
                                selectedStatus = ReadingStatus.READING
                                showStatusMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Okundu") },
                            onClick = {
                                selectedStatus = ReadingStatus.READ
                                showStatusMenu = false
                            }
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("İptal")
                    }

                    Button(
                        onClick = {
                            if (title.isNotBlank() && author.isNotBlank()) {
                                onBookAdded(
                                    Book(
                                        id = 0,
                                        title = title,
                                        author = author,
                                        pageCount = pageCount.toIntOrNull() ?: 0,
                                        status = selectedStatus
                                    )
                                )
                            }
                        },
                        enabled = title.isNotBlank() && author.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Ekle")
                    }
                }
            }
        }
    }
}
