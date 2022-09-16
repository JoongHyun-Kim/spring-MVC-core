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
#### 사용선언
```html
<html xmlns:th="http://www.thymeleaf.org">
```
<br>
<br>

#### 타임리프의 핵심
- th:xx가 붙은 부분은 서버사이드에서 렌더링되고, 기존의 것을 대체한다는 것이 핵심이다. 만약 th:xx가 붙어있지 않으면 기존 html의 xx 속성이 그대로<br>
  사용된다.
- HTML을 파일로 직접 열었을 때는 th:xx가 붙어있어도 웹 브라우저는 th 속성을 모르기 때문에 무시한다.
- 그렇기 때문에 HTML을 파일 보기를 유지하면서 템플릿 기능도 할 수 있다.
<br>
<br>

#### 속성 변경
> th:href
```html
th:href="@{/css/bootstrap.min.css}"
```
- href="value1"을 th:href="value2" 값으로 변경한다.
- 대부분의 HTML 속성을 th:xx로 변경할 수 있다.
- 타임리프 뷰 템플릿을 거치면 원래의 값을 th:xx로 변경한다. 만약 값이 없으면 새로 생성한다.
- HTML을 그대로 열어볼 때는 href 속성이 사용되지만 뷰 템플릿을 거치면 th:href의 값이 href를 대체하면서 동적으로 변경될 수 있다.
<br>
<br>

> th:onclick
```html
th:onclick="|location.href='@{/basic/items/add}'|"
```
<br>
<br>

#### 리터럴 대체 
> |...|
```html
<span th:text="|Welcome to out application, ${user.name}!|">
```
- 타임리프에서 문자와 표현식은 분리되어 있기 때문에 더해서 사용해야 하는데 리터럴 대체 문법을 사용하면 편리하게 더할 수 있다.
<br>
<br>

#### URL 링크 표현식
> @{...}
```html
th:href="@{/css/bootstrap.min.css}"
```
- URL 링크를 사용하는 경우 `@{...}`를 사용하며 이를 URL 링크 표현식이라고 한다.
- URL 링크 표현식을 사용하면 서블릿 컨텍스트를 자동으로 포함한다.(참고)
<br>
<br>

#### 반복 출력
> th:each
```html
<tr th:each="item : ${items}">
```
- 모델에 포함된 items 컬렉션의 데이터가 item 변수에 하나씩 포함되고, 반복문 안에서 item 변수를 사용할 수 있다.
- 컬렉션의 데이터 수만큼 <tr></tr>이 하위 태그를 포함해 생성된다.
<br>
<br>

#### 변수 표현식
> ${...}
```html
<td th:text="${item.price}">10000</td>
```
- 모델에 포함된 값이나 타임리프 변수로 선언한 값을 조회할 수 있다.
- 프로퍼티 접근법을 사용한다.
    - Ex) item.getPrice()
<br>
<br>

> th:text
- 값을 th:text의 값으로 변경한다.
- Ex) 10000을 ${item.price}의 값으로 변경한다.
<br>
<br>

#### 참고
> 타임리프는 순수 HTML 파일을 웹 브라우저에서 열어 내용을 확인할 수 있고, 서버를 통해 뷰 템플릿을 거치면 동적으로 변경된 결과를 확인할 수 있다. <br>
> 그런데 JSP 파일은 웹 브라우저에서 그냥 열면 JSP 소스코드와 HTML이 뒤섞여 정상적인 확인이 불가능하고 오직 서버를 통해서 JSP를 열어야 한다. <br>
> 이렇게 순수 HTML을 그대로 유지하면서 뷰 템플릿도 사용할 수 있는 타임리프의 특징을 네츄럴 템플릿 (natural templates)이라 한다.
<br>
<br>
<br>
<br>

## 상품 상세
### 상품 상세 컨트롤러
#### BasicItemController
> 코드 
```java
@GetMapping("/{itemId}")
public String item(@PathVariable Long itemId, Model model) {
    Item item = itemRepository.findById(itemId);
    model.addAttribute("item", item);
    
    return "basic/item";
}
```
- GetMapping으로 item()을 실행하도로 해주고, @PathVariable로 넘어온 상품ID를 이용해 상품을 조회하고 모델에 담은 후 뷰 템플릿을 호출한다.
<br>
<br>

### 상품 상세 뷰
#### templates/basic/item.html
```java
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="utf-8">
    <link th:href="@{/css/bootstrap.min.css}"
            href="../css/bootstrap.min.css" rel="stylesheet">
    <style>
                 .container {
            max-width: 560px;
}
    </style>
</head>
<body>
<div class="container">
    <div class="py-5 text-center">
        <h2>상품 상세</h2></div>
    <div>
        <label for="itemId">상품 ID</label>
        <input type="text" id="itemId" name="itemId" class="form-control"
               value="1" th:value="${item.id}" readonly> //value 속성을 th:value 속성으로 변경
    </div>
    <div>
        <label for="itemName">상품명</label>
        <input type="text" id="itemName" name="itemName" class="form-control"
               value="상품A" th:value="${item.itemName}" readonly></div>
    <div>
        <label for="price">가격</label>
        <input type="text" id="price" name="price" class="form-control"
               value="10000" th:value="${item.price}" readonly>
    </div>
    <div>
        <label for="quantity">수량</label>
        <input type="text" id="quantity" name="quantity" class="form-control"
               value="10" th:value="${item.quantity}" readonly>
    </div>
    <hr class="my-4">
    <div class="row">
        <div class="col">
            <button class="w-100 btn btn-primary btn-lg"
                    onclick="location.href='editForm.html'"
                    th:onclick="|location.href='@{/basic/items/{itemId}/edit(itemId=${item.id})}'|"
                    type="button">상품 수정
            </button>
        </div>
        <div class="col">
            <button class="w-100 btn btn-secondary btn-lg"
                    onclick="location.href='items.html'"
                    th:onclick="|location.href='@{/basic/items}'|"
                    type="button">목록으로
            </button>
        </div>
    </div>
</div> <!-- /container -->
</body>
</html>
```
<br>
<br>
<br>
<br>

## 상품 등록 폼
#### BasicItemController
> 코드 추가
```java
@GetMapping("/add")
public String addForm() {
   return "basic/addForm";
}
```
- @GetMapping 추가하고 뷰 템플릿만 호출한다.
<br>
<br>

### 상품 등록 뷰
#### templates/basic/addForm.html
```java
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="utf-8">
    <link th:href="@{/css/bootstrap.min.css}"
            href="../css/bootstrap.min.css" rel="stylesheet">
    <style>
          .container {
              max-width: 560px;
}
    </style>
</head>
<body>
<div class="container">
    <div class="py-5 text-center">
        <h2>상품 등록 폼</h2></div>
    <h4 class="mb-3">상품 입력</h4>

    <form action="item.html" th:action method="post"> //나중에 치환이 되면 action이 비게 되고, 그러면 그냥 현재 url(items/add)에다가 값을 넘기게 된다.(Post)
        <div>
            <label for="itemName">상품명</label>
            <input type="text" id="itemName" name="itemName" class="form-control" placeholder="이름을 입력하세요"></div>
        <div>
            <label for="price">가격</label>
            <input type="text" id="price" name="price" class="form-control" placeholder="가격을 입력하세요">
        </div>
        <div>
            <label for="quantity">수량</label>
            <input type="text" id="quantity" name="quantity" class="form-control" placeholder="수량을 입력하세요"></div>
        <hr class="my-4">
        <div class="row">
            <div class="col">
                <button class="w-100 btn btn-primary btn-lg" type="submit">상품 등록
                </button>
            </div>
            <div class="col">
                <button class="w-100 btn btn-secondary btn-lg"
                        onclick="location.href='items.html'"
                        th:onclick="|location.href='@{/basic/items}'|"
                        type="button">취소
                </button>
            </div>
        </div>
    </form>
</div> <!-- /container -->
</body>
</html>
```
- 상품 등록 폼의 URL과 실제 상품 등록을 처리하는 URL을 똑같이 설정하고 **HTTP 메소드로 두 기능을 구분한다.**
    - 상품 등록 폼 → **GET** /basic/items/add
    - 상품 등록 처리 → **POST** /basic/items/add
<br>
<br>
<br>
<br>

## 상품 등록 처리
> @ModelAttribute
- 상품 등록 폼에서 전달된 데이터로 실제 상품 등록 처리를 구현해보자!
- 상품 등록 폼은 **HTML Form 방식**으로 서버에 데이터를 전달한다.
```
content-type: application/x-www-form-urlencoded
메시지 바디에 쿼리 파라미터 형식으로 전달한다.(Ex. itemName=itemA&price=10000&quantity=10)
```
<br>
<br>

### @RequestParam
> 요청 파라미터 형식을 처리하기 위해 @RequestParam을 사용하자

#### addItemV1
> BasicItemController에 추가해 상품 등록 처리를 구현해보자!
```java
@PostMapping("/add") //같은 url로 오더라도 Get, Post로 add와 save 구분
public String addItemV1(@RequestParam String itemName,
                        @RequestParam int price,
                        @RequestParam Integer quantity,
                        Model model) {

    //파라미터가 들어오면 실제 Item 객체를 생성해 itemRepository를 통해 저장한 후 저장된 item을 모델에 담아 뷰테 전달한다.
    Item item = new Item();
    item.setItemName(itemName);
    item.setPrice(price);
    item.setQuantity(quantity);

    itemRepository.save(item);
    model.addAttribute("item", item);

    return "basic/item"; //item.html 뷰 템플릿 재활용
}
```
<br>
<br>
<br>

#### addItemV2
- @RequestParam으로 변수를 하나하나 받아 Item을 생성하는 과정은 너무 불편하므로, **@ModelAttribute**를 사용해 한 번에 처리하자!
```java
/**
 * ModelAttribute 버전
 * @ModelAttribute("item") Item item
 */
@PostMapping("/add")
public String addItemV2(@ModelAttribute("item") Item item) {

    // ModelAttribute가 이 부분을 자동으로 처리
    /*
    Item item = new Item();
    item.setItemName(itemName);
    item.setPrice(price);
    item.setQuantity(quantity);
    */

    //ModelAttribute는 1. model 객체를 만들어주고, 2. 내가 지정한 이름대로 (item) model에 담아준다.
    itemRepository.save(item);
    //model.addAttribute("item", item); //자동 추가. 생략 가능

    return "basic/item";
}
```
