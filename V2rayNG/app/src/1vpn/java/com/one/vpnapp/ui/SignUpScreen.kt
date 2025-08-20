package com.one.vpnapp.ui

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.awaitCustomerInfo
import kotlinx.coroutines.launch
import com.one.vpnapp.api.SignUpRequest
import com.one.vpnapp.api.RetrofitClient
import com.one.vpnapp.model.ErrorResponse
import com.one.vpnapp.handler.MmkvManager
import com.v2ray.ang.handler.V2RayServiceManager
import okhttp3.ResponseBody
import retrofit2.Converter
import com.v2ray.ang.R

@Composable
fun SignUpScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val usernamePasswordRequiredString = stringResource(R.string.username_password_required)
    val enterValidEmailString = stringResource(R.string.enter_valid_email)
    val unknownErrorString = stringResource(R.string.unknown_error)
    val signUpErrorString = stringResource(R.string.sign_up_error)
    val signUpSuccessfulString = stringResource(R.string.sign_up_successful)

    fun handleSubmit() {
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                context,
                usernamePasswordRequiredString,
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
                val customerInfo = Purchases.sharedInstance.awaitCustomerInfo()
                val signUpRequest = SignUpRequest(
                    username = username,
                    password = password,
                    email = email,
                    revenuecat_id = customerInfo.originalAppUserId
                )

                val response = RetrofitClient.apiService.signUp(signUpRequest)
                if (response.isSuccessful) {
                    response.body()?.let {
                        MmkvManager.setUserData(it)
                    }
                    Toast.makeText(context, signUpSuccessfulString, Toast.LENGTH_SHORT).show()

                    Purchases.sharedInstance.logIn(username)

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
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = stringResource(R.string.sign_up),
            fontSize = 26.sp,
            color = Color(0xFF333333),
            modifier = Modifier.padding(top = 24.dp)
        )
        ReusableOutlinedTextField(
            value = username,
            onValueChange = { username = it },
            labelText = stringResource(R.string.username),
            imeAction = androidx.compose.ui.text.input.ImeAction.Next,
            onImeAction = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }
        )
        ReusableOutlinedTextField(
            value = password,
            onValueChange = { password = it },
            labelText = stringResource(R.string.password),
            imeAction = androidx.compose.ui.text.input.ImeAction.Next,
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Password,
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
            onImeAction = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }
        )
        ReusableOutlinedTextField(
            value = email,
            onValueChange = { email = it },
            labelText = stringResource(R.string.email_optional),
            imeAction = androidx.compose.ui.text.input.ImeAction.Done,
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Email,
            onImeAction = {
                focusManager.clearFocus()
                handleSubmit()
            }
        )
        Button(
            onClick = { handleSubmit() },
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
                .padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF106CD5)),
            shape = RoundedCornerShape(6.dp),
            elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 0.dp)
        ) {
            Text(
                text = if (loading) stringResource(R.string.loading) else stringResource(R.string.sign_up),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White,
            )
        }

        Text(
            text = stringResource(R.string.have_an_account),
            color = Color(0xFF333333),
            modifier = Modifier
                .padding(top = 8.dp, bottom = 24.dp)
                .clickable {
                    navController.navigate("login")
                }
        )
    }
}