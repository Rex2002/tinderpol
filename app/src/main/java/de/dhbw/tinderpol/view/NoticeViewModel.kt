package de.dhbw.tinderpol.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.dhbw.tinderpol.data.NoticeRepository
import de.dhbw.tinderpol.view.NoticeUIState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException

class NoticeViewModel(private val repository: NoticeRepository, id: String): ViewModel() {
    private val _uiState = MutableStateFlow(NoticeUIState(id))
    val uiState: StateFlow<NoticeUIState> = _uiState.asStateFlow()

    private var fetchJob: Job? = null

    fun fetchArticles(id: String) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            try {
                val newsItems = repository.noticeFromID(id)
                // _uiState.update {
                    // TODO
                // }
            } catch (ioe: IOException) {
                // Handle the error and notify the UI when appropriate.
                // _uiState.update {
                //     val messages = getMessagesFromThrowable(ioe)
                //     it.copy(userMessages = messages)
                // }
            }
        }
    }

}