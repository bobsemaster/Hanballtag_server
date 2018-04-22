package de.schreib.handball.handballtag.entities

import javax.persistence.*

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

) {

}