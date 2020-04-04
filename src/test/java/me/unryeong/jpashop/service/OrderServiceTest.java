package me.unryeong.jpashop.service;

import me.unryeong.jpashop.domain.Address;
import me.unryeong.jpashop.domain.Member;
import me.unryeong.jpashop.domain.Order;
import me.unryeong.jpashop.domain.OrderStatus;
import me.unryeong.jpashop.domain.item.Book;
import me.unryeong.jpashop.domain.item.Item;
import me.unryeong.jpashop.exception.NotEnoughStockException;
import me.unryeong.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    @DisplayName("상품 주문")
    public void order() {
        // Given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000, 10);
        int orderCount = 2;

        // When
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        // Then
        Order findOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.ORDER, findOrder.getStatus(), "상품 주문시 상태는 ORDER");
        assertEquals(1, findOrder.getOrderItems().size(), "주문한 상품 종류 수가 정확해야 한다.");
        assertEquals(10000 * 2, findOrder.getTotalPrice(), "주문 가격은 가격 * 수량이다.");
        assertEquals(8, item.getStockQuantity(), "주문 수량만큼 재고가 줄어야한다.");
    }

    @Test
    @DisplayName("재고 수량 초과")
    public void stockException() {
        // Given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000, 10);
        int orderCount = 11;

        // When & Then
        assertThrows(NotEnoughStockException.class, () -> orderService.order(member.getId(), item.getId(), orderCount));

    }

    @Test
    @DisplayName("주문 취소")
    public void cancel() {
        // Given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000, 10);
        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        // When
        orderService.cancelOrder(orderId);

        // Then
        Order findOrder = orderRepository.findOne(orderId);
        assertEquals(OrderStatus.CANCEL, findOrder.getStatus(), "주문 취소시 상태는 CANCEL 이다.");
        assertEquals(10, item.getStockQuantity(), "주문 취소된 상품은 그만큼 재고가 증가해야 한다.");


    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);
        return member;
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setStockQuantity(stockQuantity);
        book.setPrice(price);
        em.persist(book);
        return book;
    }


}