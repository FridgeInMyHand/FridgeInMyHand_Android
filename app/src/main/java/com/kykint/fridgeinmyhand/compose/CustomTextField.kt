package com.kykint.fridgeinmyhand.compose

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation

/**
 * https://stackoverflow.com/a/73151375
 *
 * TextField but with flexible inner content padding control support
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: TextFieldValue,
    textStyle: TextStyle = LocalTextStyle.current,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onValueChange: (TextFieldValue) -> Unit = {},
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    singleLine: Boolean = false,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    isError: Boolean = false,
    contentPadding: PaddingValues = TextFieldDefaults.contentPaddingWithLabel(),
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle,
        modifier = modifier,
        enabled = enabled,
        /*
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = colors.backgroundColor(enabled).value,
                shape = RoundedCornerShape(8.dp)
            ),
        */
        interactionSource = interactionSource,
        singleLine = singleLine
    ) {
        TextFieldDefaults.DecorationBox(
            value = value.text,
            innerTextField = it,
            enabled = enabled,
            singleLine = singleLine,
            visualTransformation = VisualTransformation.None,
            interactionSource = interactionSource,
            label = label,
            placeholder = placeholder,
            contentPadding = contentPadding,
            container = {
                OutlinedTextFieldDefaults.ContainerBox(
                    enabled,
                    isError,
                    interactionSource,
                    colors,
                    shape,
                )
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    textStyle: TextStyle = LocalTextStyle.current,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onValueChange: (String) -> Unit = {},
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    singleLine: Boolean = false,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    isError: Boolean = false,
    contentPadding: PaddingValues = TextFieldDefaults.contentPaddingWithLabel(),
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle,
        modifier = modifier,
        enabled = enabled,
        /*
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = colors.backgroundColor(enabled).value,
                shape = RoundedCornerShape(8.dp)
            ),
        */
        interactionSource = interactionSource,
        singleLine = singleLine
    ) {
        TextFieldDefaults.DecorationBox(
            value = value,
            innerTextField = it,
            enabled = enabled,
            singleLine = singleLine,
            visualTransformation = VisualTransformation.None,
            interactionSource = interactionSource,
            label = label,
            placeholder = placeholder,
            contentPadding = contentPadding,
            container = {
                OutlinedTextFieldDefaults.ContainerBox(
                    enabled,
                    isError,
                    interactionSource,
                    colors,
                    shape,
                )
            },
        )
    }
}
