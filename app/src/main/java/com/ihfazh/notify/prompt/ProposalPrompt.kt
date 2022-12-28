package com.ihfazh.notify.prompt

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProposalPrompt(
    val id: Int,
    val label: String,
    val text: String,
    val selected: Boolean = false
): Parcelable
