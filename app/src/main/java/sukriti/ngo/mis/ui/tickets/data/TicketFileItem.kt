package sukriti.ngo.mis.ui.tickets.data

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import sukriti.ngo.mis.repository.entity.BwtProfile
import sukriti.ngo.mis.repository.entity.UsageProfile
import sukriti.ngo.mis.repository.utils.TimestampConverter
import sukriti.ngo.mis.ui.administration.AdministrationViewModel
import java.util.*

data class TicketFileItem(
    var fileType: FileType,
    var uri: Uri,
    var name: String
){

    companion object{
         enum class FileType{
            Empty,
            Photo,
            Video,
            URL_PHOTO
        }
        var _photo: FileType = FileType.Photo
        var _video: FileType = FileType.Video
        var _empty: FileType = FileType.Empty
        var _urlPhoto: FileType = FileType.URL_PHOTO
    }
}
