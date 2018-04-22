package de.schreib.handball.handballtag.entities

import java.time.Duration
import javax.persistence.*


@Entity
data class Spiel(
        @Id
        @GeneratedValue
        val id: Long,
        @ManyToOne
        val heimMannschaft: Mannschaft,
        @ManyToOne
        val gastMannschaft: Mannschaft,
        val heimTore: Int,
        val gastTore: Int,
        val hasHalfTime: Boolean,
        val halftimeDuration: Duration,

        @OneToMany(
                mappedBy = "mannschaft",
                cascade = [CascadeType.ALL],
                orphanRemoval = true,
                fetch = FetchType.EAGER

        )
        val allGeworfeneTore: List<SpielTor>
)

@Entity
data class SpielTor(
        @Id
        @GeneratedValue
        val id: Long,
        @ManyToOne
        @JoinColumn(name = "spiel_id")
        val mannschaft: Mannschaft,
        val time: Duration
)