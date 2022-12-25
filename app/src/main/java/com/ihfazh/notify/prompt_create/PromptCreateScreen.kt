package com.ihfazh.notify.prompt_create

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.ihfazh.notify.ui.theme.NotifyTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun PromptCreateScreen(
    navigator: DestinationsNavigator,
    promptCreateViewModel: PromptCreateViewModel = getViewModel()
){
    val text = promptCreateViewModel.text.collectAsState()
    val label = promptCreateViewModel.label.collectAsState()
    val selected = promptCreateViewModel.selected.collectAsState()

    NotifyTheme {
        Scaffold(
            contentColor = MaterialTheme.colorScheme.onPrimary,
            topBar = {
                TopAppBar(
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    title = { Text("Create Prompt") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.navigateUp() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, "back")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            // validate first
                            // if any error display
                            // if success navigate back (don't back, just navigate)
                        }) {
                            Icon(imageVector = Icons.Default.Done, "done")
                        }
                    }
                )
            },
            content = {
                NotifyTheme {

                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {

                        OutlinedTextField(
                            value = label.value,
                            onValueChange = { promptCreateViewModel.setLabel(it) },
                            label = { Text(text = "Label")},
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = MaterialTheme.colorScheme.onBackground,
                                backgroundColor = MaterialTheme.colorScheme.background
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = text.value,
                            onValueChange = { promptCreateViewModel.setText(it) },
                            label = { Text(text = "Text")},
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = MaterialTheme.colorScheme.onBackground,
                                backgroundColor = MaterialTheme.colorScheme.background
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable(
                                    indication = rememberRipple(color = MaterialTheme.colorScheme.primary),
                                    interactionSource = remember { MutableInteractionSource() },
                                    onClick = {
                                        promptCreateViewModel.setSelected(!selected.value)
                                    }
                                )
                                .requiredHeight(ButtonDefaults.MinHeight)
                                .padding(4.dp)
                        ) {
                            Checkbox(
                                checked = selected.value,
                                onCheckedChange = { promptCreateViewModel.setSelected(it) },
                            )
                            Spacer(Modifier.width(16.dp))
                            Text(text = "Is Selected?")
                        }

                    }

                }
            }
        )
    }
}