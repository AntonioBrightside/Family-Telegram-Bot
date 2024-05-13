package com.amam.familybot.entity;

import jakarta.persistence.*;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Entity(name = "sleep_time")
public class SleepTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "telegram_message_id")
    private long messageId;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "fall_asleep_time")
    private LocalTime fallAsleepTime;

    @Nullable
    @Column(name = "wake_up_time")
    private LocalTime wakeUpTime;

    @Nullable
    @Column(name = "sleep_time")
    private LocalTime sleepTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getFallAsleepTime() {
        return fallAsleepTime;
    }

    public void setFallAsleepTime(LocalTime fallAsleepTime) {
        this.fallAsleepTime = fallAsleepTime;
    }

    public LocalTime getWakeUpTime() {
        return wakeUpTime;
    }

    public void setWakeUpTime(LocalTime wakeUpTime) {
        this.wakeUpTime = wakeUpTime;
    }

    public LocalTime getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(LocalTime sleepTime) {
        this.sleepTime = sleepTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SleepTime sleepTime1 = (SleepTime) o;
        return id == sleepTime1.id && messageId == sleepTime1.messageId && Objects.equals(date, sleepTime1.date) && Objects.equals(fallAsleepTime, sleepTime1.fallAsleepTime) && Objects.equals(wakeUpTime, sleepTime1.wakeUpTime) && Objects.equals(sleepTime, sleepTime1.sleepTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, messageId, date, fallAsleepTime, wakeUpTime, sleepTime);
    }

    @Override
    public String toString() {
        return "SleepTime{" +
                "id=" + id +
                ", messageId=" + messageId +
                ", date=" + date +
                ", fallAsleepTime=" + fallAsleepTime +
                ", wakeUpTime=" + wakeUpTime +
                ", sleepTime=" + sleepTime +
                '}';
    }
}


