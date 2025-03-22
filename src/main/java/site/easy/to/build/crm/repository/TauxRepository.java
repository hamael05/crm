package site.easy.to.build.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import site.easy.to.build.crm.entity.Taux;

@Repository
public interface TauxRepository extends JpaRepository<Taux,Integer> {

    @Query("select t from Taux t order by t.id desc limit 1")
    Taux getLast();
}
