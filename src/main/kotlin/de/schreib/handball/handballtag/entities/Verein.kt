package de.schreib.handball.handballtag.entities

import javax.persistence.*

/**
 * In einem Verein werden alle mannschaften gespeichert, die von diesem Verein angemeldet sind, außerdem wird der Name
 * des Vereins noch abgespeichert.
 */
@Entity
data class Verein(
        @Id
        @GeneratedValue
        val id: Long,
        @OneToMany(
                mappedBy = "verein",
                cascade = [CascadeType.ALL],
                orphanRemoval = true
        )
        val allMannschaft: List<Mannschaft>,
        val name: String

)