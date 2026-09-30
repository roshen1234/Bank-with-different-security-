package com.eazybytes.springsecsection1.doa;

import com.eazybytes.springsecsection1.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact,String> {


}