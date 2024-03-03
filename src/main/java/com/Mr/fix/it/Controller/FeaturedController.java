package com.Mr.fix.it.Controller;

import lombok.RequiredArgsConstructor;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;

import com.Mr.fix.it.Service.FeaturedService;

import com.Mr.fix.it.Response.FeaturedResponse;

import com.Mr.fix.it.Exception.ExceptionType.NotFoundException;

@Validated
@RestController
@RequestMapping("/api/worker")
@RequiredArgsConstructor
public class FeaturedController
{
    private final FeaturedService featuredService;

    @GetMapping("/get-featured")
    public ResponseEntity<FeaturedResponse> getFeatured(
        @RequestHeader(name="Authorization")
        String token
    ) throws NotFoundException
    {
        return ResponseEntity.ok(
            featuredService.getFeatured(token.substring(7))
        );
    }

    @GetMapping("/subscribe-featured")
    public ModelAndView subscribeFeatured(
        @NotEmpty(message = "missing subscription status")
        @RequestParam String subscriptionStatus,
        @NotEmpty(message = "missing worker id")
        @RequestParam String workerID,
        ModelAndView modelAndView
    ) throws NotFoundException
    {
        modelAndView.setViewName("response");

        if(subscriptionStatus.equals("success"))
        {
            featuredService.subscribeFeatured(Long.parseLong(workerID));

            modelAndView.addObject("status", "Succeeded!");
            modelAndView.addObject("message", "Subscribed successfully.");
        }
        else if(subscriptionStatus.equals("cancel"))
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
