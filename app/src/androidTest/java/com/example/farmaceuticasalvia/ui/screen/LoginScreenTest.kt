package com.example.farmaceuticasalvia.ui.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_muestra_elementos_basicos() {
        composeTestRule.setContent {

            LoginScreen(
                email = "", pass = "",
                emailError = null, passError = null,
                canSubmit = false, isSubmitting = false, errorMsg = null,
                onEmailChange = {}, onPassChange = {}, onSubmit = {}, onGoRegister = {}
            )
        }

        composeTestRule.onNodeWithText("Inicio de Sesión").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
    }

    @Test
    fun loginScreen_muestra_error_si_se_le_pasa() {
        composeTestRule.setContent {
            LoginScreen(
                email = "test", pass = "123",
                emailError = "Correo inválido",
                passError = null,
                canSubmit = false, isSubmitting = false, errorMsg = null,
                onEmailChange = {}, onPassChange = {}, onSubmit = {}, onGoRegister = {}
            )
        }


        composeTestRule.onNodeWithText("Correo inválido").assertIsDisplayed()
    }

    @Test
    fun loginScreen_permite_escribir() {


        composeTestRule.setContent {
            LoginScreen(
                email = "", pass = "", emailError = null, passError = null,
                canSubmit = false, isSubmitting = false, errorMsg = null,
                onEmailChange = {}, onPassChange = {}, onSubmit = {}, onGoRegister = {}
            )
        }


        composeTestRule.onNodeWithTag("emailInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("emailInput").performTextInput("usuario@test.com")


    }
}