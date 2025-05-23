package com.dreams.hellowordspring.reservation.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccesDeniedController {
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
