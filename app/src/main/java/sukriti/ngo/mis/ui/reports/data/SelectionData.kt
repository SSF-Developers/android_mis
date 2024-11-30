package sukriti.ngo.mis.ui.reports.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import sukriti.ngo.mis.repository.entity.BwtProfile
import sukriti.ngo.mis.repository.entity.UsageProfile
import sukriti.ngo.mis.repository.utils.TimestampConverter
import sukriti.ngo.mis.ui.administration.AdministrationViewModel
import java.util.*

data class SelectionData(
    var states: List<String>,
    var districts: List<String>,
    var cities: List<String>,
    var complexes: List<String>
)
