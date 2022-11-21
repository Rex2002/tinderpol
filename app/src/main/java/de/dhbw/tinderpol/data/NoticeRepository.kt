package de.dhbw.tinderpol.data

class NoticeRepository() {
    private val localDataSource = LocalNoticesDataSource()
    private val remoteDataSource = RemoteNoticesDataSource()


    fun noticeFromID(id: String) {

    }
}