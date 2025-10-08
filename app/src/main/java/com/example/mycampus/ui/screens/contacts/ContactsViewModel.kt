package com.example.mycampus.ui.screens.contacts

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycampus.domain.model.Contact
import com.example.mycampus.domain.repository.ContactsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val contactsRepository: ContactsRepository
) : ViewModel() {

    private val _contactsState = mutableStateOf(ContactsState())
    val contactsState: State<ContactsState> = _contactsState

    init {
        loadContacts()
    }

    fun loadContacts() {
        viewModelScope.launch {
            _contactsState.value = _contactsState.value.copy(
                isLoading = true,
                error = null
            )
            try {
                val contacts = contactsRepository.getContacts()
                _contactsState.value = _contactsState.value.copy(
                    contacts = contacts,
                    isLoading = false
                )
            } catch (e: Exception) {
                _contactsState.value = _contactsState.value.copy(
                    error = e.message ?: "Erreur de chargement des contacts",
                    isLoading = false
                )
            }
        }
    }

    fun openDialer(phoneNumber: String, context: Context) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = "tel:$phoneNumber".toUri()
        }
        context.startActivity(intent)
    }


    fun openSmsApp(phoneNumber: String, context: Context) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "smsto:$phoneNumber".toUri()    }
        context.startActivity(intent)
    }

}

data class ContactsState(
    val contacts: List<Contact> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
