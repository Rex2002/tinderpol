package de.dhbw.tinderpol.data.room

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import de.dhbw.tinderpol.data.Charge
import de.dhbw.tinderpol.data.Notice

data class NoticeWithLists(
    @Embedded
    var notice: Notice,

    @Relation(parentColumn = "id", entityColumn = "noticeId")
    var noticeNationalityMaps: List<NoticeNationalityMap>,

    @Relation(parentColumn = "id", entityColumn = "noticeId")
    var noticeImageMaps: List<NoticeImageMap>,

    @Relation(parentColumn = "id", entityColumn = "noticeId")
    var spokenLanguages: List<NoticeLanguageMap>,

    @Relation(parentColumn = "id", entity = Charge::class, entityColumn = "id",
        associateBy = Junction(NoticeChargeMap::class, parentColumn = "noticeId", entityColumn = "chargeId"))
    var charges: List<Charge>
) {

    fun getLanguages(): List<String>{
        val languages: MutableList<String> = mutableListOf()
        spokenLanguages.forEach { languages.add(it.language) }
        return languages.toList()
    }
    fun getImages(): List<String>{
        val images: MutableList<String> = mutableListOf()
        noticeImageMaps.forEach { images.add(it.image) }
        return images.toList()
    }
    fun getNationalities(): List<String>{
        val nationalities: MutableList<String> = mutableListOf()
        noticeNationalityMaps.forEach { nationalities.add(it.nationality) }
        return nationalities.toList()
    }
}
