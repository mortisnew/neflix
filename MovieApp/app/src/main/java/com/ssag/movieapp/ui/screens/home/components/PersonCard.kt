package com.ssag.movieapp.ui.screens.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ssag.movieapp.data.model.PersonDto
import com.ssag.movieapp.ui.theme.FlixElevatedSurface
import com.ssag.movieapp.util.Constants

@Composable
fun PersonCard(
    person: PersonDto,
    onClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .width(90.dp)
            .clickable { onClick(person.id) }
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = FlixElevatedSurface,
            modifier = Modifier.size(80.dp),
            shadowElevation = 4.dp
        ) {
            AsyncImage(
                model = "${Constants.BASE_URL}people/people/${person.id}/avatar/",
                contentDescription = person.full_name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = person.full_name,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = Color.White
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}
