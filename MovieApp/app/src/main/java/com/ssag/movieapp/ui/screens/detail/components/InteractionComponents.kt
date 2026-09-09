package com.ssag.movieapp.ui.screens.detail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssag.movieapp.data.model.CommentDto
import com.ssag.movieapp.ui.theme.FlixPrimary
import com.ssag.movieapp.ui.theme.FlixSurface


@Composable
fun RatingSection(
    imdbRating: String,
    userRating: Int?,
    onRate: (Int) -> Unit
) {
    var selectedRating by remember(userRating) {
        mutableIntStateOf(userRating ?: 0)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "IMDb Rating",
            tint = Color(0xFFFFD600),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = imdbRating,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )

        Text(
            text = " / 10 (IMDb)",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            (1..5).forEach { index ->

                val ratingValue = index

                Icon(
                    imageVector = if (selectedRating >= ratingValue) {
                        Icons.Default.Star
                    } else {
                        Icons.Default.StarBorder
                    },
                    contentDescription = "Rate $ratingValue",
                    tint = FlixPrimary,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                            selectedRating = ratingValue
                            onRate(ratingValue)
                        }
                )
            }
        }
    }
}


@Composable
fun CommentsSection(
    comments: List<CommentDto>,
    onAddComment: (String) -> Unit
) {
    var newComment by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Comments",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = newComment,
            onValueChange = {
                newComment = it
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "Add a comment...",
                    color = Color.Gray
                )
            },
            minLines = 2,
            maxLines = 5,
            trailingIcon = {
                val canSend = newComment.trim().isNotEmpty()

                IconButton(
                    enabled = canSend,
                    onClick = {
                        val commentText = newComment.trim()

                        if (commentText.isNotEmpty()) {
                            onAddComment(commentText)
                            newComment = ""
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send Comment",
                        tint = if (canSend) {
                            FlixPrimary
                        } else {
                            Color.DarkGray
                        }
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FlixPrimary,
                unfocusedBorderColor = Color.DarkGray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = FlixPrimary
            ),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (comments.isEmpty()) {
            Text(
                text = "No comments yet.",
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                textAlign = TextAlign.Center
            )
        } else {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                comments.forEach { comment ->
                    CommentItem(comment)
                }
            }
        }
    }
}


@Composable
private fun CommentItem(
    comment: CommentDto
) {
    Surface(
        color = FlixSurface,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = comment.username ?: "Guest",
                color = Color.White,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = comment.comment,
                color = Color.LightGray,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

