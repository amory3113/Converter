package com.example.converter.presentation

fun getFlagUrl(currencyCode: String): String {
    val countryCode = when(currencyCode.uppercase()){
        "EUR" -> "eu"
        "ANG" -> "cw"
        "XCG" -> "cw"
        "XAF" -> "cm"
        "XCD" -> "ag"
        "XOF" -> "sn"
        "XPF" -> "pf"
        "XDR" -> "un"
        else -> currencyCode.take(2).lowercase()
    }
    return "https://flagcdn.com/w80/$countryCode.png"
}