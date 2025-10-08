package com.example.mycampus.domain.repository

import com.example.mycampus.domain.model.Contact
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ContactsRepository @Inject constructor() {


    private val contactsList = listOf(
        Contact(
            id = "1",
            name = "Service des Urgences",
            role = "Sécurité & Santé",
            phoneNumber = "112",
            email = null,
            department = "Campus Central",
            avatarColor = 0xFFE57373 // Rouge clair
        ),
        Contact(
            id = "2",
            name = "Accueil Principal",
            role = "Information & Orientation",
            phoneNumber = "0123456789",
            email = "accueil@mycampus.edu",
            department = "Administration",
            avatarColor = 0xFF64B5F6 // Bleu clair
        ),
        Contact(
            id = "3",
            name = "Support Informatique",
            role = "Assistance Technique",
            phoneNumber = "9876543210",
            email = "support.it@mycampus.edu",
            department = "Services Techniques",
            avatarColor = 0xFF81C784 // Vert clair
        )

    )

    fun getContacts(): List<Contact> {
        return contactsList
    }
}
