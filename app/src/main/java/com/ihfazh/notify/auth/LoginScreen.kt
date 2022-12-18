package com.ihfazh.notify.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ihfazh.notify.ui.component.TextInput
import com.ihfazh.notify.ui.theme.NotifyTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import org.koin.androidx.compose.getViewModel

@Destination
@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = getViewModel(),
    resultNavigator: ResultBackNavigator<Boolean>
){
    val username = loginViewModel.username.collectAsState()
    val password = loginViewModel.password.collectAsState()
    val context = LocalContext.current

    NotifyTheme {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text(
                "Login",
                textAlign = TextAlign.Center,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                fontStyle = MaterialTheme.typography.headlineLarge.fontStyle
            )
            Spacer(modifier = Modifier.height(24.dp))
            TextInput(
                value = username.value,
                onChange = {
                    loginViewModel.setUsername(it)
                },
                label = "Username",
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextInput(
                value = password.value,
                onChange = {
                    loginViewModel.setPassword(it)
                },
                label = "Password",
                isPassword = true
            )
            Spacer(modifier = Modifier.height(40.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    loginViewModel.login{
                        if (it){
                            resultNavigator.navigateBack(result=true)
                        } else {
                            Toast.makeText(context, loginViewModel.errorString.value, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            ) {
                Text(
                    "Masuk",
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                )
            }
        }
    }

}