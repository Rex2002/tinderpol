package de.dhbw.tinderpol.data

data class APIResponse(
    val data: List<Notice>,
    val err: String?,
    val lastUpdate: Double,
)
