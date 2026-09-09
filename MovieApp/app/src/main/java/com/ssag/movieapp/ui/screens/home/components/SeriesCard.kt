package com.ssag.movieapp.ui.screens.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ssag.movieapp.data.model.SeriesDto
import com.ssag.movieapp.ui.theme.FlixElevatedSurface
import com.ssag.movieapp.util.Constants

@Composable
fun SeriesCard(
    series: SeriesDto,
    onClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .width(130.dp)
            .clickable { onClick(series.id) }
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .height(190.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = FlixElevatedSurface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            AsyncImage(
                model = "${Constants.BASE_URL}content/series/${series.id}/poster_url/",
                contentDescription = series.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = series.title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Color.White
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = series.release_date.take(4),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = "•",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = "⭐ ${series.imdb_rating}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
