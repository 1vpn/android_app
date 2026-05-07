package com.one.vpnapp.ui

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.one.vpnapp.api.RetrofitClient
import com.one.vpnapp.api.SignUpRequest
import com.one.vpnapp.handler.MmkvManager
import com.one.vpnapp.model.ErrorResponse
import com.revenuecat.purchases.Purchases
import com.v2ray.ang.R
import com.v2ray.ang.handler.V2RayServiceManager
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Converter

@Composable
fun SignUpScreen(navController: NavController, initialEmail: String = "") {
    var email by remember { mutableStateOf(initialEmail) }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val emailPasswordRequiredString = stringResource(R.string.email_password_required)
    val enterValidEmailString = stringResource(R.string.enter_valid_email)
    val unknownErrorString = stringResource(R.string.unknown_error)
    val signUpErrorString = stringResource(R.string.sign_up_error)
    val signUpSuccessfulString = stringResource(R.string.sign_up_successful)

    fun handleSubmit() {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                context,
                emailPasswordRequiredString,
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, enterValidEmailString, Toast.LENGTH_SHORT).show()
            return
        }

        loading = true
        coroutineScope.launch {
            try {
                val signUpRequest = SignUpRequest(
                    email = email,
                    password = password,
                )

                val response = RetrofitClient.callWithFallback { it.signUp(signUpRequest) }
                if (response.isSuccessful) {
                    response.body()?.let {
                        MmkvManager.setUserData(it)
                    }
                    Toast.makeText(context, signUpSuccessfulString, Toast.LENGTH_SHORT).show()

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
                    val errorResponse: ErrorResponse? = errorBody?.let { converter.convert(it) }
                    val errorMsg = errorResponse?.error ?: unknownErrorString
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, signUpErrorString, Toast.LENGTH_SHORT).show()
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
            text = stringResource(R.string.sign_up),
            fontSize = 26.sp,
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
        )
        ReusableOutlinedTextField(
            value = email,
            onValueChange = { email = it },
            labelText = stringResource(R.string.email),
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )
        ReusableOutlinedTextField(
            value = password,
            onValueChange = { password = it },
            labelText = stringResource(R.string.password),
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Password,
            visualTransformation = PasswordVisualTransformation(),
            onImeAction = {
                focusManager.clearFocus()
                handleSubmit()
            }
        )
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
                text = if (loading) stringResource(R.string.loading) else stringResource(R.string.sign_up),
                fontSize = 14.sp,
                color = white,
            )
        }

        Text(
            text = stringResource(R.string.have_an_account),
            color = black,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 24.dp)
                .clickable {
                    navController.navigate("login")
                }
        )
    }
}