package de.dhbw.tinderpol.data

class NoticeRepository() {
    private val localDataSource = LocalNoticesDataSource()
    private val remoteDataSource = RemoteNoticesDataSource()

    suspend fun fetchNotices(): List<Notice> {
        if (localDataSource.lastUpdated() > 100) {
            localDataSource.update(remoteDataSource.fetchNotices())
        }
        return localDataSource.fetchNotices()
    }
}