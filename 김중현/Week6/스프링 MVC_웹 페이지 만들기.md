# Section 7. 스프링 MVC - 웹 페이지 만들기
> 상품 관리 서비스를 제작해보자!

## 요구사항 분석
> 상품 관리 서비스 
#### 상품 도메인 모델
```
상품 ID
상품명
가격
수량
```
<br>
<br>

#### 상품 관리 기능
```
상품 목록
상품 상세
상품 등록
상품 수정
```
<br>
<br>

#### 서비스 제공 흐름 정리
<img width="700" alt="스크린샷 2022-09-01 오후 12 34 21" src="https://user-images.githubusercontent.com/80838501/187830014-17c36994-b703-4ebd-ae0b-2e1b45894542.png">
<br>
<br>
<br>
<br>

## 상품 도메인 개발
#### Item
> 상품 객체
```java
package hello.itemservice.domain.item;

import lombok.Data;

@Data
public class Item {
    private Long id;
    private String itemName;
    private Integer price;
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
```
- 상품 ID, 상품명, 가격, 수량
- 생성자
<br>
<br>

#### ItemRepository
> 상품 저장소
```java
package hello.itemservice.domain.item;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemRepository {

    private static final Map<Long, Item> store = new HashMap<>();
    private static long sequence = 0L;

    public Item save(Item item) {
        item.setId(++sequence);
        store.put(item.getId(), item);

        return item;
    }

    public Item findById(Long id) {
        return store.get(id);
    }

    public List<Item> findAll() {
        return new ArrayList<>(store.values());
    }

    public void update(Long itemId, Item updateParam) {
        Item findItem = findById(itemId);
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
    }

    public void clearStore() {
        store.clear();
    }
}
```
- 저장, Id로 조회, 전체 조회, 수정, 전체 clear(테스트용)
<br>
<br>

#### ItemRepositoryTest
> 상품 저장소 테스트
```java
package hello.itemservice.domain.item;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class ItemRepositoryTest {

    ItemRepository itemRepository = new ItemRepository();

    @AfterEach
    void afterEach() { //테스트 돌 때마다 clear
        itemRepository.clearStore();
    }

    @Test
    void save() {
        //given
        Item item = new Item("itemA", 10000, 10);

        //when
        Item savedItem = itemRepository.save(item);

        //then
        Item findItem = itemRepository.findById(item.getId());
        assertThat(findItem).isEqualTo(savedItem);
    }

    @Test
    void findAll() {
        //given
        Item item1 = new Item("item1", 10000, 10);
        Item item2 = new Item("item2", 20000, 20);

        itemRepository.save(item1);
        itemRepository.save(item2);

        //when
        List<Item> result = itemRepository.findAll();

        //then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(item1, item2);
    }

    @Test
    void updateItem() {
        //given
        Item item = new Item("item1", 10000, 10);

        Item savedItem = itemRepository.save(item);
        Long itemId = savedItem.getId();

        //when
        Item updateParam = new Item("item2", 20000, 30);
        itemRepository.update(itemId, updateParam);

        //then
        Item findItem = itemRepository.findById(itemId);

        assertThat(findItem.getItemName()).isEqualTo(updateParam.getItemName());
        assertThat(findItem.getPrice()).isEqualTo(updateParam.getPrice());
        assertThat(findItem.getQuantity()).isEqualTo(updateParam.getQuantity());
    }
}
```
- 저장, 전체 조회, 수정 테스트 
<br>
<br>
<br>
<br>

## 상품 목록 - 타임리프
#### BasicItemController
```java
package hello.itemservice.web.basic;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import java.util.List;

@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor
public class BasicItemController {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) { // "/basic/items" 경로로 GET이 오면 이 메소드가 호출
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items); //model에 "items"라고 컬렉션이 담긴다.
        return "basic/item"; //view 보여주기
    }

    /**
     * 테스트용 데이터 추가
     */
    @PostConstruct
    public void init() {
        itemRepository.save(new Item("itemA", 10000, 10));
        itemRepository.save(new Item("itemB", 20000, 20));
    }
}
```
- itemRepository에서 모든 상품을 조회한 뒤 모델에 담고, 뷰 템플릿을 호출한다.
- @PostConstruct: 헤당 빈의 의존관계가 모두 주입되고 나면 초기화를 위해 호출된다.
- `@RequiredArgsConstructor` 애노테이션: final이 붙은 필드의 생성자를 자동 생성해주는 롬복 애노테이션
```java
@Controller
@RequestMapping("/basic/items")
public class BasicItemController {

    private final ItemRepository itemRepository;

    @Autowired
    public BasicItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }
}
```

```java
@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor
public class BasicItemController {

    private final ItemRepository itemRepository;
}
```
→ 롬복이 itemRepository의 생성자를 자동으로 생성해준다. <br>
→ 생성자가 1개만 있으면 스프링이 그 생성자에 `@Autowired`로 의존관계를 주입해준다.
<br>
<br>
<br>

> 정적 HTML으로 작성한 items.html을 뷰 템플릿 영역으로 복사해오고 타임리프를 적용해 코드를 수정하자!
#### templates/basic/items.html
```java
<html xmlns:th="http://wwww.thymeleaf.org">
<head>
    <meta charset="utf-8">
    <link th:href="@{/css/bootstrap.min.css}"
            href="../css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container" style="max-width: 600px">
    <div class="py-5 text-center">
        <h2>상품 목록</h2></div>
    <div class="row">
        <div class="col">
            <button class="btn btn-primary float-end"
                    onclick="location.href='addForm.html'"
                    th:onclick="|location.href='@{/basic/items/add}'|"
                    type="button">상품 등록
            </button>
        </div>
    </div>
    <hr class="my-4">
    <div>
        <table class="table">
            <thead>
            <tr>
                <th>ID</th>
                <th>상품명</th>
                <th>가격</th>
                <th>수량</th>
            </tr>
            </thead>
            <tbody>
            <tr th:each="item : ${items}">
                <td><a href="item.html" th:href="@{/basic/items/{itemId}(itemId=${item.id})}" th:text="${item.id}">회원id</a></td>
                <td><a href="item.html" th:href="@{/basic/items/{itemId}(itemId=${item.id})}" th:text="${item.id}">상품명</a></td>
                <td th:text="${item.price}">10000</td>
                <td th:text="${item.quantity}">10</td>
            </tr>
            </tbody>
        </table>
    </div>
</div> <!-- /container -->
</body>
</html>
```
<br>

### 타임리프
