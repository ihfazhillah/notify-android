package com.ihfazh.notify.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TextInput(
    modifier: Modifier = Modifier,
    label: String? = null,
    onChange: (String) -> Unit = {},
    onIconClicked: () -> Unit = {},
    iconPainter: Painter? = null,
    value: String,
    placeHolder: String = "",
    isPassword: Boolean = false
){

    Column(modifier = modifier) {
        if (label != null) {
            Text(text = label, fontSize = MaterialTheme.typography.body1.fontSize)
            Spacer(modifier = Modifier.height(10.dp))
        }


        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            trailingIcon = {
                if (iconPainter != null) {
                    Image(
                        iconPainter,
                        contentDescription = "Test",
                        modifier = Modifier
                            .clickable(onClick = onIconClicked)
                            .height(24.dp)
                            .width(24.dp)
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder =  {
                Text(placeHolder, fontSize = 14.sp)
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = MaterialTheme.colors.primary
            ),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
        )
    }
}

