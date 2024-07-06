package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@DataJpaTest
@Sql(value = {"/set-up-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/set-up-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ItemRepositoryIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;

    private int userId;
    private int amountItems;
    private PageRequest defaultPageRequest;

    @BeforeEach
    public void setUp() {
        defaultPageRequest = PageRequest.of(0, 10);
        userId = 1;
        amountItems = 3;
    }

    @Test
    public void findAllByUserIdWhenPageRequestDefaultReturnThreeItems() {
        assertEquals(amountItems, itemRepository.findAllByOwnerId(userId, defaultPageRequest).size());
    }

    @Test
    public void findAllByUserIdWhenPageRequestSizeIsTwoReturnTwoItems() {
        assertEquals(2, itemRepository.findAllByOwnerId(userId, PageRequest.of(0, 2)).size());
    }

    @Test
    public void findAllByUserIdWhenUserNotFoundReturnEmptyList() {
        int unknownUser = 100;

        assertEquals(0, itemRepository.findAllByOwnerId(unknownUser, defaultPageRequest).size());
    }

    @Test
    public void findAllByUserIdWhenUserDontHaveItemsReturnEmptyList() {
        int userWithoutItems = 2;

        assertEquals(0, itemRepository.findAllByOwnerId(userWithoutItems, defaultPageRequest).size());
    }

    @Test
    public void findAllByAvailableAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCaseWhenMethodInvokedReturnOneItem() {
        assertEquals(1, itemRepository
                .findAllByAvailableAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(true,
                        "отвертка", "отвертка", defaultPageRequest).size());
    }

    @Test
    public void findAllByAvailableAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCaseWhenOnlyDescriptionMatchReturnTwoItems() {
        assertEquals(2, itemRepository
                .findAllByAvailableAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(true,
                        "прост", "прост", defaultPageRequest).size());
    }

    @Test
    public void findAllByAvailableAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCaseWhenItemsNotFoundReturnEmptyList() {
        assertEquals(0, itemRepository
                .findAllByAvailableAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(true,
                        "Бу", "Бу", defaultPageRequest).size());
    }

    @Test
    public void findAllByAvailableAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCaseWhenAvailableFalseReturnOneItem() {
        assertEquals(1, itemRepository
                .findAllByAvailableAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(false,
                        "Бенз", "Бенз", defaultPageRequest).size());
    }

    @Test
    public void findAllByRequestIdWhenInvokedMethodReturnTwoItems() {
        assertEquals(2, itemRepository.findAllByRequestId(1).size());
    }

    @Test
    public void findAllByRequestIdWhenItemsNotFoundReturnEmptyList() {
        int unknownRequestId = 100;

        assertEquals(0, itemRepository.findAllByRequestId(unknownRequestId).size());
    }

    @Test
    public void findAllByRequestsWhenItemsNotFoundByListReturnEmptyList() {
        int unknownRequestId = 100;

        List<Integer> requests = List.of(unknownRequestId);

        assertEquals(0, itemRepository.findAllByRequests(requests).size());
    }

    @Test
    public void findAllByRequestsWhenTwoItemsFoundReturnTwoItems() {
        int requestId = 1;

        List<Integer> requests = List.of(requestId);

        assertEquals(2, itemRepository.findAllByRequests(requests).size());
    }
}
