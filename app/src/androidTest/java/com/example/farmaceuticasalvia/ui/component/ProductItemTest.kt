package com.example.farmaceuticasalvia.ui.component

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.farmaceuticasalvia.data.remote.dto.ProductoResponse
import com.example.farmaceuticasalvia.ui.screen.ProductItem
import org.junit.Rule
import org.junit.Test

class ProductItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun productItem_muestra_informacion_correcta() {

        val fakeProduct = ProductoResponse(
            id = 1,
            sku = "TEST-100",
            nombre = "Paracetamol Test",
            descripcion = "Cura todo",
            precio = 1000.0,
            imagenUrl = "http://fake.url",
            destacado = false,
            pideReceta = false
        )


        composeTestRule.setContent {
            ProductItem(
                product = fakeProduct,
                onBuyClick = {},
                onCartClick = {}
            )
        }


        composeTestRule.onNodeWithText("Paracetamol Test").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cura todo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Precio: $1000").assertIsDisplayed()
    }

    @Test
    fun productItem_botones_existen_y_son_clickeables() {
        val fakeProduct = ProductoResponse(1, "A", "B", "C", 10.0, "", false, false)
        var buyClicked = false

        composeTestRule.setContent {
            ProductItem(
                product = fakeProduct,
                onBuyClick = { buyClicked = true },
                onCartClick = {}
            )
        }

        composeTestRule.onNodeWithText("Comprar").performClick()

        assert(buyClicked)
    }
}