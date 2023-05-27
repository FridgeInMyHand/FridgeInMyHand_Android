package com.kykint.composestudy.compose

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kykint.composestudy.ui.theme.ComposeStudyTheme

@Composable
fun ComposableToast(message: String) {
    Toast.makeText(LocalContext.current, message, Toast.LENGTH_SHORT).show()
}

@Composable
fun MyElevatedCard(
    modifier: Modifier = Modifier,
    cardHorizontalPadding: Dp = 8.dp,
    cardVerticalPadding: Dp = 8.dp,
    colors: CardColors = CardDefaults.elevatedCardColors(
        containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation: CardElevation = CardDefaults.cardElevation(2.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .padding(horizontal = cardHorizontalPadding, vertical = cardVerticalPadding),
    ) {
        Card(
            modifier = modifier,
            elevation = elevation,
            colors = colors,
            content = content,
        )
    }
}


@Preview(showBackground = true)
@Composable
fun MyElevatedCardPreview() {
    ComposeStudyTheme {
        MyElevatedCard(
            content = {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(32.dp),
                ) { Text("Text") }
            }
        )
    }
}

@Composable
fun ProgressDialog(content: @Composable (RowScope.() -> Unit)? = null) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
    ) {
        Surface(
            modifier = Modifier.wrapContentSize(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(24.dp),
            ) {
                CircularProgressIndicator()
                content?.let {
                    Spacer(modifier = Modifier.width(16.dp))
                    it()
                }
            }
        }
    }
}

@Preview
@Composable
fun ProgressDialogPreview() {
    ComposeStudyTheme {
        ProgressDialog { Text("Waiting for server...") }
    }
}
