package com.neocompany.taroro.domain.room.entity;

import java.time.Instant;

import com.neocompany.taroro.global.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "room_card_reading")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomCardReading extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "set_id")
    private Long setId;

    @Column(name = "card_id", nullable = false)
    private Long cardId;

    @Column(name = "position", nullable = false)
    private Integer position;

    @Column(name = "is_picked", nullable = false)
    private boolean isPicked = false;

    @Column(name = "is_revealed", nullable = false)
    private boolean isRevealed = false;

    @Column(name = "picked_at")
    private Instant pickedAt;

    @Column(name = "revealed_at")
    private Instant revealedAt;

    public static RoomCardReading create(Long roomId, Long setId, Long cardId, int position) {
        RoomCardReading r = new RoomCardReading();
        r.roomId = roomId;
        r.setId = setId;
        r.cardId = cardId;
        r.position = position;
        return r;
    }

    public void pick() {
        this.isPicked = true;
        this.pickedAt = Instant.now();
    }

    public void reveal() {
        this.isRevealed = true;
        this.revealedAt = Instant.now();
    }
}
