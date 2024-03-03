package com.Mr.fix.it.Response;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsResponse
{
    private int clients;

    private float clientsPercentage;

    private List<Integer> clientsValues;

    private List<String> clientsCategories;

    private int workers;

    private float workersPercentage;

    private List<Integer> workersValues;

    private List<String> workersCategories;

    private int adsRevenue;

    private float adsRevenuePercentage;

    private List<Integer> adsRevenueValues;

    private List<String> adsRevenueCategories;

    private int featuredRevenue;

    private float featuredRevenuePercentage;

    private List<Integer> featuredRevenueValues;

    private List<String> featuredRevenueCategories;

    private int donationRevenue;

    private float donationRevenuePercentage;

    private List<Integer> donationRevenueValues;

    private List<String> donationRevenueCategories;
}
