package com.eazybytes.springsecsection1.doa;

import com.eazybytes.springsecsection1.entity.Loans;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoansRepository extends JpaRepository<Loans,Long> {
    List<Loans> findByCustomerIdOrderByStartDtDesc(long customerId);
}
