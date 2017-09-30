package com.softserve.academy.spaced.repetition.domain;

import com.softserve.academy.spaced.repetition.DTO.EntityInterface;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Card")
public class Card implements EntityInterface {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "card_id")
    private Long id;

    @Column(name = "question", nullable = false, columnDefinition = "LONGTEXT")
    private String question;

    @Column(name = "answer", nullable = false, columnDefinition = "LONGTEXT")
    private String answer;

    @Column(name = "rating")
    private double rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id")
    private Deck deck;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL)
    private List<CardRating> cardRatings;

    public Card() {
    }

    public Card(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public Card(Long id, String question, String answer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public List<CardRating> getCardRatings() {
        return cardRatings;
    }

    public void setCardRatings(List<CardRating> cardRatings) {
        this.cardRatings = cardRatings;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }
}
