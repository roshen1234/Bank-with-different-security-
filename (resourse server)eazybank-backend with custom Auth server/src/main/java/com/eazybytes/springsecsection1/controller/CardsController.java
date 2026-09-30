package com.eazybytes.springsecsection1.controller;

import com.eazybytes.springsecsection1.doa.CardsRepository;
import com.eazybytes.springsecsection1.doa.UserDAO;
import com.eazybytes.springsecsection1.entity.Cards;
import com.eazybytes.springsecsection1.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CardsController {

    private final CardsRepository cardsRepository;
    private final UserDAO userDAO;

    @GetMapping("/myCards")
    public List<Cards> getCardDetails(@RequestParam String email) {
        User user=userDAO.findUserByUsername(email);

        if(user!=null) {
            List<Cards> cards = cardsRepository.findByCustomerId(user.getId());
            if (cards != null) {
                return cards;
            } else {
                return null;
            }
        }
        else
            return null;
    }

}
