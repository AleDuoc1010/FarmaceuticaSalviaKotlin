package com.example.farmaceuticasalvia.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MindicadorResponse(
    val dolar: Indicador,
    val uf: Indicador
)

data class Indicador(
    val codigo: String,
    val nombre: String,
    @SerializedName("valor")
    val valor: Double
)