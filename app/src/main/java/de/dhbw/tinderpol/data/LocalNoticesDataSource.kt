package de.dhbw.tinderpol.data

class LocalNoticesDataSource {
    fun lastUpdated(): Int {
        return 10
    }

    fun update(notices: List<Notice>) {
        // TODO
    }

    fun fetchNotices(noticeDao: NoticeDao): List<Notice> {
        return noticeDao.getAll()
    }
}