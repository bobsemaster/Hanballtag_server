package de.schreib.handball.handballtag.entities

import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
data class Verkauf(
        @Id
        @GeneratedValue
        val id: Long = 0,
        @OneToMany(
                fetch = FetchType.EAGER
        )
        val verkaufArtikel: List<VerkaufArtikel> = listOf(),
        val isLosverkaufGestartet: Boolean = false,
        val isPreisvergabeGestartet: Boolean = false,
        val isGrillAn: Boolean = false
)

@Entity
data class VerkaufArtikel(
        @Id
        @GeneratedValue
        val id: Long = 0,
        val artikelName: String,
        val artikelPreis: Double,
        val verkaufsplatz: String
)