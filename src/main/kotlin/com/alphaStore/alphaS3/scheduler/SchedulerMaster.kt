package com.alphaStore.alphaS3.scheduler

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class SchedulerMaster (
){
    @Scheduled(cron = "*/120 * * * * *")
    fun runsEvery10Seconds() {

    }

    // Example method to run every 5 minutes
    @Scheduled(cron = "0 */5 * * * *")
    fun runsEvery5Minutes() {
        //println("Running every 5 minutes...")
    }

    // Example method to run every 2 hours
    @Scheduled(cron = "0 0 */2 * * *")
    fun runsEvery2Hours() {
        //println("Running every 2 hours...")
    }

    // Example method to run every midnight (12:00 AM)
    @Scheduled(cron = "0 0 0 * * *")
    fun runsAtMidnight() {
        //println("Running at midnight...")
    }
}