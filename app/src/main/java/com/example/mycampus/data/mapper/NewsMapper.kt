package com.example.mycampus.data.mapper

import com.example.mycampus.data.local.entity.NewsEntity
import com.example.mycampus.data.remote.dto.NewsDto
import com.example.mycampus.domain.model.News

/**
 * Convertit un objet NewsDto (provenant de l'API réseau)
 * en un objet NewsEntity (pour la base de données locale).
 */
fun NewsDto.toNewsEntity(): NewsEntity {
    return NewsEntity(
        id = this.id,
        title = this.title,
        description = this.description,
        date = this.date,
        // CORRECTION FINALE : Utilise 'image' au lieu de 'imageUrl'
        imageUrl = this.image
    )
}

/**
 * Convertit un objet NewsEntity (provenant de la base de données locale)
 * en un objet News (le modèle de domaine utilisé par l'UI).
 */
fun NewsEntity.toNews(): News {
    return News(
        id = this.id,
        title = this.title,
        description = this.description,
        date = this.date,
        imageUrl = this.imageUrl ?: "" // On garde imageUrl ici, car c'est le nom du champ dans NewsEntity
    )
}
