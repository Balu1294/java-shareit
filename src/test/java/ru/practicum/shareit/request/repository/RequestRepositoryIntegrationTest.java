package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Sql(value = {"/set-up-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/set-up-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class RequestRepositoryIntegrationTest {

    @Autowired
    private RequestRepository requestRepository;

    private int userId;
    private int userWithoutRequests;

    @BeforeEach
    public void setUp() {
        userId = 2;
        userWithoutRequests = 1;
    }

    @Test
    public void getItemRequestsByUserWhenInvokedMethodReturnListItemRequests() {
        assertEquals(3, requestRepository.findAllByRequestorId(userId).size());
    }

    @Test
    public void getItemRequestsByUserWhenItemRequestsNotFoundReturnEmptyList() {
        assertEquals(0, requestRepository.findAllByRequestorId(userWithoutRequests).size());
    }

    @Test
    public void findAllByUserUserIdNotLikeWhenInvokedMethodReturnEmptyList() {
        assertEquals(0, requestRepository.findAllByRequestorIdNotLike(userId, PageRequest.of(0, 10)).size());
    }

    @Test
    public void findAllByUserUserIdNotLikeWhenInvokedMethodReturnListWithThreeRequests() {
        assertEquals(3, requestRepository.findAllByRequestorIdNotLike(userWithoutRequests, PageRequest.of(0, 10)).size());
    }
}
