package de.schreib.handball.handballtag.entities

import de.schreib.handball.handballtag.enums.JugendEnum
import de.schreib.handball.handballtag.enums.JugendGender
import javax.persistence.*

/**
 * Diese Entity bildet eine Jugend ab z.b. männl. C-Jugend und speichert alle mannschaften die in dieser Jugend spielen dazu ab
 * Ausserdem ist dazu noch die tabelle dieser Jugend verfügbar
 */
@Embeddable
data class Jugend(
        val typ: JugendGender,
        val jahrgang: JugendEnum
)
