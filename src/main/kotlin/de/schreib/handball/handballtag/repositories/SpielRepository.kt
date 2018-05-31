package de.schreib.handball.handballtag.repositories

import de.schreib.handball.handballtag.entities.Mannschaft
import de.schreib.handball.handballtag.entities.Spiel
import org.intellij.lang.annotations.Language
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SpielRepository : JpaRepository<Spiel, Long> {

    @Language("SpringDataQL")
    @Query(value = "SELECT s FROM Spiel s WHERE s.heimMannschaft= ?1 OR s.gastMannschaft = ?1")
    fun findAllByMannschaft(mannschaft: Mannschaft): List<Spiel>

    fun deleteAllByHeimMannschaftInOrGastMannschaftIn(mannschaftHeim: Collection<Mannschaft>, mannschaftGast: Collection<Mannschaft>)

}
