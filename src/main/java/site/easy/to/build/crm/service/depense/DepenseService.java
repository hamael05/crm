package site.easy.to.build.crm.service.depense;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.entity.Depense;
import site.easy.to.build.crm.repository.DepenseRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DepenseService {

    DepenseRepository repository;

    public DepenseService(DepenseRepository repository) {
        this.repository = repository;
    }

    public void save(Depense depense) { repository.save(depense); }

    public List<Depense> findAll() { return repository.findAll(); }

    public List<Depense> findAllByCustomerId(int customerId) { return repository.findAllByCustomerId(customerId); }

    public double sommeDepenseByCustomerId(int customerId) {
        List<Depense> depenses = repository.findByLeadCustomerCustomerIdOrTicketCustomerCustomerId(customerId, customerId);
        BigDecimal sum = BigDecimal.ZERO;
        for (Depense depense : depenses) {
            sum = sum.add(depense.getAmount());
        }
        return sum.doubleValue();
    }
}
