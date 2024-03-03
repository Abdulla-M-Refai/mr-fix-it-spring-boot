package com.Mr.fix.it.Controller;

import lombok.RequiredArgsConstructor;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import org.springframework.web.servlet.ModelAndView;

import com.Mr.fix.it.Service.ClientDonationService;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
@Validated
public class ClientDonationController
{
    private final ClientDonationService clientDonationService;

    @GetMapping("/client-donation")
    public ModelAndView clientDonation(
        @NotEmpty(message = "missing donation status")
        @RequestParam String donationStatus,
        @NotEmpty(message = "missing client id")
        @RequestParam String clientID,
        @NotEmpty(message = "missing amount")
        @RequestParam String amount,
        ModelAndView modelAndView
    )
    {
        modelAndView.setViewName("response");

        if(donationStatus.equals("success"))
        {
            clientDonationService.saveClientDonation(Long.parseLong(clientID), Integer.parseInt(amount));

            modelAndView.addObject("status", "Succeeded!");
            modelAndView.addObject("message", "Thank you for your donation.");
        }
        else if(donationStatus.equals("cancel"))
        {
            modelAndView.addObject("status", "Canceled!");
            modelAndView.addObject("message", "Your request canceled.");
        }
        else
        {
            modelAndView.addObject("status", "Unknown Status!");
            modelAndView.addObject("message", "Unknown request status.");
        }

        return modelAndView;
    }
}
