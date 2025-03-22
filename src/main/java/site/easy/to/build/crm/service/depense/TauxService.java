package site.easy.to.build.crm.service.depense;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.entity.Taux;
import site.easy.to.build.crm.repository.TauxRepository;

@Service
public class TauxService {

    @Autowired
    TauxRepository tauxRepository;

    public void save(Taux taux) { tauxRepository.save(taux); }

    public Taux getLast() { return tauxRepository.getLast(); }

}
