package sukriti.ngo.mis.ui.administration.data

data class RawProvisioningTreeNode(
    var isComplex: Boolean = false,
    var complex: RawComplex?,
    var nonComplex: RawNonComplex?
)

