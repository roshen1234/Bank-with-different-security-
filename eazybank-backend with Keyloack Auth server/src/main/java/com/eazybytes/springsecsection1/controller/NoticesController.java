package com.eazybytes.springsecsection1.controller;

import com.eazybytes.springsecsection1.doa.NoticesRepository;
import com.eazybytes.springsecsection1.entity.Notice;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
public class NoticesController {

    private final NoticesRepository noticeRepository;

    @GetMapping("/notices")
    public ResponseEntity<List<Notice>> getNotices() {
        List<Notice> notices = noticeRepository.findAllActiveNotices();
        if (notices != null) {
            return ResponseEntity.ok()
                    //since notices is always rendering same data each time we reload we dont need to send data from backend again and again we can store it in a cache (browser cache) for 60 second and send from there till 60 second ends no baceknd call is done
                    .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                    .body(notices);
        } else {
            return null;
        }
    }

}