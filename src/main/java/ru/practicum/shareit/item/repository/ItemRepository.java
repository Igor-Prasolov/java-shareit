package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    //    Item save(Item item);
    //    Item update(Long id, Item newItem);
    //    Optional<Item> findItemById(Long id);

    List<Item> findAllItemByOwnerId(Long id);
    //    List<Item> findAll();
    //    void deleteItem(Long id);

    @Query("SELECT i FROM Item AS i WHERE (upper(i.name) LIKE upper(concat('%', ?1, '%')) " +
            "OR upper (i.description) LIKE upper(concat('%', ?1, '%'))) " +
            "AND i.available = true")
    List<Item> search(String text);
}
