package site.easy.to.build.crm.repository;

import jakarta.persistence.Id;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import site.easy.to.build.crm.entity.Depense;
import site.easy.to.build.crm.entity.Lead;

import java.util.List;

@Repository
public interface DepenseRepository extends JpaRepository<Depense, Integer> {

    @Query("select d from Depense d where d.lead.customer.customerId = :customerId or d.ticket.customer.customerId = :customerId")
    List<Depense> findAllByCustomerId(int customerId);

    List<Depense> findByLeadCustomerCustomerIdOrTicketCustomerCustomerId(int leadCustomerId, int ticketCustomerId);
}
