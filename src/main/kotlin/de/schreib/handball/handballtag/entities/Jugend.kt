package de.schreib.handball.handballtag.entities

import de.schreib.handball.handballtag.enums.JugendEnum
import de.schreib.handball.handballtag.enums.JugendGender
import javax.persistence.*

/**
 * Diese Entity bildet eine Jugend ab z.b. männl. C-Jugend und speichert alle mannschaften die in dieser Jugend spielen dazu ab
 * Ausserdem ist dazu noch die tabelle dieser Jugend verfügbar
 */
@Entity
data class Jugend(
        @Id
        @GeneratedValue
        val id: Long,
        @OneToMany(
                mappedBy = "jugend",
                cascade = [CascadeType.ALL],
                orphanRemoval = true
        )
        val allMannschaft: List<Mannschaft>,
        val typ: JugendGender,
        val name: JugendEnum,
        @OneToOne
        @JoinColumn(name = "tabelle_id")
        val tabelle: Tabelle
)
