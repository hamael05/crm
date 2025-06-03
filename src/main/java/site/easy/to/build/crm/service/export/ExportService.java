package site.easy.to.build.crm.service.export;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.entity.*;
import site.easy.to.build.crm.service.customer.CustomerService;
import site.easy.to.build.crm.service.depense.BudgetService;
import site.easy.to.build.crm.service.depense.DepenseService;
import site.easy.to.build.crm.service.lead.LeadService;
import site.easy.to.build.crm.service.ticket.TicketService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class ExportService {

    @Autowired
    CustomerService customerService;
    @Autowired
    LeadService leadService;
    @Autowired
    TicketService ticketService;
    @Autowired
    DepenseService depenseService;
    @Autowired
    BudgetService budgetService;

    public void exportCustomer(String data, User user) {
        String[] split = data.split("/");

        String[] customerSrting = split[0].split(";");

        Customer customer = new Customer();

        customer.setName(customerSrting[1].replace("name:", ""));
        customer.setEmail(customerSrting[2].replace("email:", ""));
        customer.setCreatedAt(LocalDateTime.now());
        customer.setCountry("Madagascar");
        customer.setPhone("123");
        customer.setUser(user);
        customerService.save(customer);
    }

    public void exportBudget(String data, Customer customer) {
        String[] split = data.split("/");

        String[] budgetString = split[1].split("%");
        for (int i = 0; i < budgetString.length; i++) {
            Budget budget = new Budget();
            if (!budgetString[i].equals("")) {
                budget.setCustomer(customer);
                budget.setCreatedAt(LocalDateTime.now());
                budget.setAmount(budgetString[i].split(";")[1].replace("amount:", ""));
                budgetService.save(budget);
            }
        }
    }

    public void exportDepense(String data, User user, Customer customer) {
        String[] split = data.split("/");

        String[] depenseSrting = split[2].split("%");
        for (int i = 0; i < depenseSrting.length; i++) {
            String[] depenseLigne = depenseSrting[i].split(";");
            if (!depenseSrting[i].equals("")) {
                Depense depense = new Depense();
                depense.setCreatedAt(LocalDateTime.now());
                depense.setAmount(BigDecimal.valueOf(Double.parseDouble(depenseLigne[1].replace("amount:", ""))));
                if (depenseLigne[2].replace("lead:", "").equals("null")) {
                    Ticket ticket = new Ticket();
                    ticket.setCustomer(customer);
                    ticket.setSubject(depenseLigne[5].replace("subject_name:", ""));
                    ticket.setCreatedAt(LocalDateTime.now());
                    ticket.setStatus(depenseLigne[4].replace("status:", ""));
                    ticket.setAmount(Double.parseDouble(depenseLigne[1].replace("amount:", "")));
                    ticket.setPriority("low");
                    ticket.setEmployee(user);

                    ticketService.save(ticket);

                    depense.setTicket(ticket);
                    depenseService.save(depense);
                }
                if (depenseLigne[3].replace("ticket:", "").equals("null")) {
                    Lead lead = new Lead();
                    lead.setCustomer(customer);
                    lead.setName(depenseLigne[5].replace("subject_name:", ""));
                    lead.setCreatedAt(LocalDateTime.now());
                    lead.setStatus(depenseLigne[4].replace("status:", ""));
                    lead.setAmount(Double.parseDouble(depenseLigne[1].replace("amount:", "")));
                    lead.setEmployee(user);

                    leadService.save(lead);

                    depense.setLead(lead);
                    depenseService.save(depense);
                }
            }
        }
    }
}
