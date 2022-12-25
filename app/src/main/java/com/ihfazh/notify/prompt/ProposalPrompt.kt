package com.ihfazh.notify.prompt

data class ProposalPrompt(
    val id: Int,
    val label: String,
    val text: String,
    val selected: Boolean = false
)
