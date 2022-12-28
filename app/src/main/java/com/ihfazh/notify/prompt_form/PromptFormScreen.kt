package com.ihfazh.notify.prompt_form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.ihfazh.notify.destinations.PromptScreenDestination
import com.ihfazh.notify.prompt.ProposalPrompt
import com.ihfazh.notify.ui.theme.NotifyTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialNavigationApi::class,
    ExperimentalMaterialApi::class
)
@Destination
@Composable
fun PromptFormScreen(
    prompt: ProposalPrompt? = null,
    navigator: DestinationsNavigator,
    promptFormViewModel: PromptFormViewModel = getViewModel()
){
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberNavController(bottomSheetNavigator)

    if (prompt != null){
        promptFormViewModel.setPrompt(prompt)
    }

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded}
    )

    val title = if (prompt != null) "Update Prompt" else "Create Prompt"


    val globalError = remember {
        mutableStateOf("")
    }

    NotifyTheme {
        Scaffold(
            contentColor = MaterialTheme.colorScheme.onPrimary,
            topBar = {
                TopAppBar(
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    title = { Text(title) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.navigateUp() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, "back")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            if (promptFormViewModel.validate()){
                                promptFormViewModel.submit {
                                    if (it){
                                        navigator.navigate(PromptScreenDestination)
                                    } else {
                                        globalError.value = "Saving data into server error. Please Try again."
                                    }
                                }
                            }
                        }) {
                            Icon(imageVector = Icons.Default.Done, "done")
                        }
                    }
                )
            },
            content = {
                NotifyTheme {
                    ModalBottomSheetLayout(
                        sheetState = sheetState,
                        sheetContent = {
                            BottomSheet(
                                promptFormViewModel,
                                sheetState
                            )
                        },
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        PromptForm(
                            promptFormViewModel,
                            sheetState,
                            globalError
                        )
                    }
                }

            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PromptForm(
    promptFormViewModel: PromptFormViewModel,
    sheetState: ModalBottomSheetState,
    globalError: MutableState<String>
) {
    val text = promptFormViewModel.text.collectAsState()
    val label = promptFormViewModel.label.collectAsState()
    val selected = promptFormViewModel.selected.collectAsState()

    // errors
    val textError = promptFormViewModel.textError.collectAsState()
    val labelError = promptFormViewModel.labelError.collectAsState()


    // preview
    val previewText = promptFormViewModel.previewText.collectAsState()
    val previewLoading = promptFormViewModel.previewLoading.collectAsState()

    val previewButtonText = if (previewLoading.value) "Generating Preview... " else "Generate Preview"


    val scope = rememberCoroutineScope()

    Column(
        Modifier.padding(16.dp)
    ) {

        if (globalError.value.isNotEmpty()) {
            Text(
                globalError.value,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        OutlinedTextField(
            value = label.value,
            onValueChange = { promptFormViewModel.setLabel(it) },
            label = { Text(text = "Label") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            colors = TextFieldDefaults.textFieldColors(
                textColor = MaterialTheme.colorScheme.onBackground,
                backgroundColor = MaterialTheme.colorScheme.background
            ),
            modifier = Modifier.fillMaxWidth(),
            isError = labelError.value.isNotEmpty()
        )
        if (labelError.value.isNotEmpty()) {
            Text(
                labelError.value,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = text.value,
            onValueChange = { promptFormViewModel.setText(it) },
            label = { Text(text = "Text") },
            colors = TextFieldDefaults.textFieldColors(
                textColor = MaterialTheme.colorScheme.onBackground,
                backgroundColor = MaterialTheme.colorScheme.background
            ),
            isError = textError.value.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
        )
        if (textError.value.isNotEmpty()) {
            Text(
                textError.value,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(
                    indication = rememberRipple(color = MaterialTheme.colorScheme.primary),
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {
                        promptFormViewModel.setSelected(!selected.value)
                    }
                )
                .requiredHeight(ButtonDefaults.MinHeight)
                .padding(4.dp)
        ) {
            Checkbox(
                checked = selected.value,
                onCheckedChange = { promptFormViewModel.setSelected(it) },
            )
            Spacer(Modifier.width(16.dp))
            Text(text = "Is Selected?")
        }

        Spacer(modifier = Modifier.height(32.dp))
        androidx.compose.material3.Button(
            onClick = {
                scope.launch {
                    promptFormViewModel.getPreview(text.value)
                    sheetState.show()
                }
//                                promptCreateViewModel.getPreview(text.value)
//                                      navigator.navigate(HelloBottomScreenDestination)
            },
            enabled = !previewLoading.value
        ) {
            Text(previewButtonText)
        }
    }
}


@OptIn(ExperimentalMaterialApi::class, ExperimentalUnitApi::class)
@Composable
fun BottomSheet(
    viewModel: PromptFormViewModel,
    sheetState: ModalBottomSheetState
){

    val scope = rememberCoroutineScope()

    val jobDesc = viewModel.jobDescText.collectAsState()
    val preview = viewModel.previewText.collectAsState()
    val previewLoading = viewModel.previewLoading.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {

        if (!previewLoading.value){
            Text(
                "Job Description",
                fontSize = androidx.compose.material3.MaterialTheme.typography.titleMedium.fontSize,
                fontWeight = androidx.compose.material3.MaterialTheme.typography.titleMedium.fontWeight,
                fontStyle = androidx.compose.material3.MaterialTheme.typography.titleMedium.fontStyle,
            )
            Text(jobDesc.value, lineHeight = TextUnit(1.5f, TextUnitType.Em))

            Spacer(Modifier.height(32.dp))

            Text(
                "Proposal",
                fontSize = androidx.compose.material3.MaterialTheme.typography.titleMedium.fontSize,
                fontWeight = androidx.compose.material3.MaterialTheme.typography.titleMedium.fontWeight,
                fontStyle = androidx.compose.material3.MaterialTheme.typography.titleMedium.fontStyle,
            )
            Text(preview.value, lineHeight = TextUnit(1.5f, TextUnitType.Em))
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularProgressIndicator()
                Spacer(Modifier.width(16.dp))
                Text("Loading...")
            }
        }

    }

}