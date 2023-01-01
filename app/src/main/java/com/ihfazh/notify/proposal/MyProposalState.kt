package com.ihfazh.notify.proposal

sealed interface  MyProposalState {
    object Initial: MyProposalState
    object Loading: MyProposalState
    object Draft: MyProposalState
    object Saving: MyProposalState
    object Saved: MyProposalState
    data class Error(val message: String? = null): MyProposalState
}