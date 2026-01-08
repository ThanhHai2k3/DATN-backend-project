package com.backend.jobservice.scheduler;

import com.backend.jobservice.repository.InternshipPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class InternshipPostExpirationScheduler {

    private final InternshipPostRepository internshipPostRepository;

    @Scheduled(cron = "0 */5 * * * *") // mỗi 5 phút
    @Transactional
    public void expirePosts() {
        Instant now = Instant.now();
        int affected = internshipPostRepository.markExpiredPosts(now);

        if (affected > 0) {
            log.info("Expired {} internship posts at {}", affected, now);
        }
    }
}