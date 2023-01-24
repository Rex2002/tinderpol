package de.dhbw.tinderpol.data.room

import androidx.room.Embedded
import androidx.room.Relation
import de.dhbw.tinderpol.data.Charge
import de.dhbw.tinderpol.data.Notice

data class NoticeWithLists(
    @Embedded
    var notice: Notice,

    @Relation(parentColumn = "id", entityColumn = "noticeId")
    var noticeNationalities: List<NoticeNationality>,

    @Relation(parentColumn = "id", entityColumn = "noticeId")
    var noticeImages: List<NoticeImage>,

    @Relation(parentColumn = "id", entityColumn = "noticeId")
    var spokenLanguages: List<NoticeLanguage>,

    @Relation(parentColumn = "id", entityColumn = "noticeId")
    var noticeCharges: List<NoticeCharge>
) {

    fun getLanguages(): List<String>{
        val languages: MutableList<String> = mutableListOf()
        spokenLanguages.forEach { languages.add(it.language) }
        return languages.toList()
    }
    fun getImages(): List<String>{
        val images: MutableList<String> = mutableListOf()
        noticeImages.forEach { images.add(it.image) }
        return images.toList()
    }
    fun getNationalities(): List<String>{
        val nationalities: MutableList<String> = mutableListOf()
        noticeNationalities.forEach { nationalities.add(it.nationality) }
        return nationalities.toList()
    }

    fun getCharges(): List<Charge> {
        val charges: MutableList<Charge> = mutableListOf()
        noticeCharges.forEach { charges.add( Charge(it.country, it.charge)) }
        return charges.toList()
    }
}
