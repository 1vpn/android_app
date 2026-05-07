package com.one.vpnapp.ui

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.one.vpnapp.api.LoginRequest
import com.one.vpnapp.api.RetrofitClient
import com.one.vpnapp.handler.MmkvManager
import com.one.vpnapp.model.ErrorResponse
import com.revenuecat.purchases.Purchases
import com.v2ray.ang.R
import com.v2ray.ang.handler.V2RayServiceManager
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Converter

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }
    var showTokenInput by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val usernamePasswordRequiredMsg = stringResource(R.string.email_password_required)
    val loginSuccessfulString = stringResource(R.string.login_successful)
    val loginErrorString = stringResource(R.string.login_error)
    val unknownErrorString = stringResource(R.string.unknown_error)

    fun handleSubmit() {
        if (email.isEmpty() || email.isEmpty()) {
            Toast.makeText(context, usernamePasswordRequiredMsg, Toast.LENGTH_SHORT).show()
            return
        }
        loading = true
        coroutineScope.launch {
            try {
                val loginRequest = if (token.isNotEmpty()) {
                    LoginRequest(email, password, token)
                } else {
                    LoginRequest(email, password)
                }
                val response = RetrofitClient.callWithFallback { it.login(loginRequest) }
                if (response.isSuccessful) {
                    val userData = response.body()
                    userData?.let {
                        MmkvManager.setUserData(it)
                    }
                    Toast.makeText(context, loginSuccessfulString, Toast.LENGTH_SHORT).show()

                    Purchases.sharedInstance.logIn(email)

                    MmkvManager.removeSelectedLocation()
                    V2RayServiceManager.stopVService(context)

                    if (context is MainActivity) {
                        context.recreate()
                    }

                    navController.navigate("main")
                } else {
                    val errorBody = response.errorBody()
                    val converter: Converter<ResponseBody, ErrorResponse> =
                        RetrofitClient.retrofit.responseBodyConverter(
                            ErrorResponse::class.java,
                            arrayOf()
                        )
                    val errorResponse: ErrorResponse? = errorBody?.let { error: ResponseBody ->
                        converter.convert(error)
                    }
                    if (errorResponse != null && errorResponse.code == 1001 && !showTokenInput) {
                        showTokenInput = true
                    } else {
                        val errorMsg = errorResponse?.error ?: unknownErrorString
                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, loginErrorString, Toast.LENGTH_SHORT).show()
            } finally {
                loading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .imePadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = stringResource(R.string.login),
            fontSize = 26.sp,
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
        )
        ReusableOutlinedTextField(
            value = email,
            onValueChange = { email = it },
            labelText = stringResource(R.string.email),
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
            onImeAction = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }
        )
        ReusableOutlinedTextField(
            value = password,
            onValueChange = { password = it },
            labelText = stringResource(R.string.password),
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Password,
            visualTransformation = PasswordVisualTransformation(),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    handleSubmit()
                }
            )
        )
        if (showTokenInput) {
            ReusableOutlinedTextField(
                value = token,
                onValueChange = { token = it },
                labelText = stringResource(R.string.twofa_token),
                imeAction = ImeAction.Done,
                onImeAction = {
                    focusManager.clearFocus()
                    handleSubmit()
                }
            )
        }
        Button(
            onClick = { handleSubmit() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = blue),
            shape = RoundedCornerShape(8.dp),
            elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 0.dp)
        ) {
            Text(
                text = if (loading) stringResource(R.string.loading) else stringResource(R.string.login),
                fontSize = 14.sp,
                color = white,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.create_account),
                color = black,
                fontSize = 14.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .clickable {
                        navController.navigate("signUp")
                    }
            )
            Text(
                text = stringResource(R.string.forgot_password),
                color = black,
                fontSize = 14.sp,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .clickable {
                        val browserIntent = Intent(
                            Intent.ACTION_VIEW,
                            "${RetrofitClient.activeBaseUrl()}/password_reset_request".toUri()
                        )
                        context.startActivity(browserIntent)
                    }
            )
        }
    }
}