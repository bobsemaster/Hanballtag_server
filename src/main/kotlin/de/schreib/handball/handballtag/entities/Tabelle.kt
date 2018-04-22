package de.schreib.handball.handballtag.entities

import javax.persistence.*

@Entity
data class Tabelle(
        @Id
        @GeneratedValue
        val id: Long,
        @OneToMany(
                mappedBy = "verein",
                cascade = [CascadeType.ALL],
                orphanRemoval = true

        )
        val allMannschaft: List<Mannschaft>,
        @OneToOne
        @JoinColumn(name = "jugend_id")
        val jugend: Jugend

) {
}
