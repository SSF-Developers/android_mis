package sukriti.ngo.mis.ui.complexes.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import sukriti.ngo.mis.repository.entity.BwtProfile
import sukriti.ngo.mis.repository.entity.UsageProfile
import sukriti.ngo.mis.repository.utils.TimestampConverter
import sukriti.ngo.mis.ui.administration.AdministrationViewModel
import java.util.*

data class DisplayBwtProfile(
    var bwtProfile: BwtProfile,
    val date: String,
    val time: String
)
