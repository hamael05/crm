package site.easy.to.build.crm.service.depense;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.repository.BudgetRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BudgetService {

    @Autowired
    BudgetRepository budgetRepository;

    public void save(Budget budget) { budgetRepository.save(budget); }

    public List<Budget> findAll() { return budgetRepository.findAll(); }

    public List<Budget> findAllByCustomerId(int customerId) { return budgetRepository.findAllByCustomerId(customerId); }

    public double sommeBudgetByCustomer(int customerId) {
        List<Budget> budgets = budgetRepository.findAllByCustomerId(customerId);
        BigDecimal sum = BigDecimal.ZERO;
        for (Budget budget : budgets) {
            sum = sum.add(budget.getAmount());
        }
        return sum.doubleValue();
    }
}
