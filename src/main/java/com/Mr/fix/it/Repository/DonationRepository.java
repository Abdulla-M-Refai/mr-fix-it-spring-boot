package com.Mr.fix.it.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Mr.fix.it.Entity.Donation;

public interface DonationRepository extends JpaRepository<Donation, Long>
{
}