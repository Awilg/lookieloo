package com.lookieloo.ui.model

data class Filter(
    val name: String? = null,
    val type: FilterType? = null,
    val enabled: Boolean = false
)

enum class FilterType {
    Accessible,
    Baby,
    Clean,
    Genderless,
    Public
}