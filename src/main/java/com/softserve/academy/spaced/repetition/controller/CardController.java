package com.softserve.academy.spaced.repetition.controller;

import com.softserve.academy.spaced.repetition.controller.dto.impl.CardPublicDTO;
import com.softserve.academy.spaced.repetition.controller.dto.simpleDTO.CardDTO;
import com.softserve.academy.spaced.repetition.domain.Card;
import com.softserve.academy.spaced.repetition.service.CardService;
import com.softserve.academy.spaced.repetition.utils.audit.Auditable;
import com.softserve.academy.spaced.repetition.utils.audit.AuditingAction;
import com.softserve.academy.spaced.repetition.utils.exceptions.NotAuthorisedUserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.softserve.academy.spaced.repetition.controller.dto.builder.DTOBuilder.buildDtoForEntity;
import static com.softserve.academy.spaced.repetition.controller.dto.builder.DTOBuilder.buildDtoListForCollection;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/decks/{deckId}/")
public class CardController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CardController.class);

    @Autowired
    private CardService cardService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("learn")
    public List<CardPublicDTO> getLearningCards(@PathVariable Long deckId) throws NotAuthorisedUserException {
        return buildDtoListForCollection(cardService.getLearningCards(deckId),
                CardPublicDTO.class, linkTo(methodOn(DeckController.class).getCardsByDeck(deckId)).withSelfRel());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("learn/additional")
    public List<CardPublicDTO> getAdditionalLearningCards(@PathVariable Long deckId)
            throws NotAuthorisedUserException {
        return buildDtoListForCollection(cardService.getAdditionalLearningCards(deckId),
                CardPublicDTO.class, linkTo(methodOn(DeckController.class).getCardsByDeck(deckId)).withSelfRel());
    }

    //TODO security
    @GetMapping("not-postponed")
    @ResponseStatus(HttpStatus.OK)
    public Boolean areThereNotPostponedCardsAvailable(@PathVariable Long deckId)
            throws NotAuthorisedUserException {
        return cardService.areThereNotPostponedCardsAvailable(deckId);
    }

    @Auditable(action = AuditingAction.CREATE_CARD_VIA_CATEGORY_AND_DECK)
    @PostMapping(value = "cards")
    @ResponseStatus(HttpStatus.CREATED)
    public CardPublicDTO addCard(@PathVariable Long deckId, @RequestBody CardDTO card) {
        LOGGER.debug("Add card to deckId: {}", deckId);
        Card newCard = new Card(card.getTitle(), card.getQuestion(), card.getAnswer());
        cardService.addCard(newCard, deckId, card.getImages());
        return buildDtoForEntity(newCard, CardPublicDTO.class,
                linkTo(methodOn(CardController.class).getCardById(deckId, newCard.getId())).withSelfRel());
    }

    @Auditable(action = AuditingAction.EDIT_CARD_VIA_CATEGORY_AND_DECK)
    @PutMapping(value = "/api/decks/{deckId}/cards/{cardId}")
    @ResponseStatus(HttpStatus.OK)
    public CardPublicDTO updateCard(@PathVariable Long deckId,
                                    @PathVariable Long cardId,
                                    @RequestBody CardDTO card) {
        LOGGER.debug("Updating card with id: {}  in deck with id: {}", cardId, deckId);
        return buildDtoForEntity(cardService.updateCard(new Card(card.getTitle(), card.getQuestion(),
                        card.getAnswer()), cardId, card.getImages()),
                CardPublicDTO.class, linkTo(methodOn(CardController.class).getCardById(deckId, cardId)).withSelfRel());
    }

    @Auditable(action = AuditingAction.DELETE_CARD)
    @DeleteMapping(value = "cards/{cardId}")
    public void deleteCard(@PathVariable Long cardId) {
        cardService.deleteCard(cardId);
    }

    @GetMapping(value = "cards/{cardId}")
    @ResponseStatus(HttpStatus.OK)
    public CardPublicDTO getCardById(@PathVariable Long deckId, @PathVariable Long cardId) {
        return buildDtoForEntity(cardService.getCard(cardId), CardPublicDTO.class,
                linkTo(methodOn(CardController.class).getCardById(deckId, cardId)).withSelfRel());
    }
}
