package com.ssag.movieapp.ui.screens.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssag.movieapp.data.model.CountryDto
import com.ssag.movieapp.data.model.GenreDto
import com.ssag.movieapp.ui.theme.FlixPrimary
import com.ssag.movieapp.ui.theme.FlixSurface

@Composable
fun FilterBar(
    genres: List<GenreDto>,
    countries: List<CountryDto>,
    selectedGenre: GenreDto?,
    selectedCountry: CountryDto?,
    selectedSort: String?,
    onGenreSelected: (GenreDto?) -> Unit,
    onCountrySelected: (CountryDto?) -> Unit,
    onSortSelected: (String?) -> Unit,
    onClearFilters: () -> Unit
) {
    var showGenreMenu by remember { mutableStateOf(false) }
    var showCountryMenu by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }

    val sortOptions = listOf(
        "Newest" to "newest",
        "Oldest" to "oldest",
        "Most Popular" to "popular",
        "Top Rated" to "rating"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Genre
        FilterChip(
            label = selectedGenre?.name ?: "Genre",
            isSelected = selectedGenre != null,
            onClick = { showGenreMenu = true }
        )
        DropdownMenu(expanded = showGenreMenu, onDismissRequest = { showGenreMenu = false }) {
            DropdownMenuItem(text = { Text("All Genres") }, onClick = { onGenreSelected(null); showGenreMenu = false })
            genres.forEach { genre ->
                DropdownMenuItem(text = { Text(genre.name) }, onClick = { onGenreSelected(genre); showGenreMenu = false })
            }
        }

        // Country
        FilterChip(
            label = selectedCountry?.name ?: "Country",
            isSelected = selectedCountry != null,
            onClick = { showCountryMenu = true }
        )
        DropdownMenu(expanded = showCountryMenu, onDismissRequest = { showCountryMenu = false }) {
            DropdownMenuItem(text = { Text("All Countries") }, onClick = { onCountrySelected(null); showCountryMenu = false })
            countries.forEach { country ->
                DropdownMenuItem(text = { Text(country.name) }, onClick = { onCountrySelected(country); showCountryMenu = false })
            }
        }

        // Sort
        FilterChip(
            label = sortOptions.find { it.second == selectedSort }?.first ?: "Sort",
            isSelected = selectedSort != null,
            onClick = { showSortMenu = true }
        )
        DropdownMenu(expanded = showSortMenu, onDismissRequest = { showSortMenu = false }) {
            sortOptions.forEach { (label, value) ->
                DropdownMenuItem(text = { Text(label) }, onClick = { onSortSelected(value); showSortMenu = false })
            }
        }

        if (selectedGenre != null || selectedCountry != null || selectedSort != null) {
            TextButton(onClick = onClearFilters) {
                Text("Clear", color = FlixPrimary, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) FlixPrimary.copy(alpha = 0.2f) else FlixSurface,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, FlixPrimary) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = if (isSelected) FlixPrimary else Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
