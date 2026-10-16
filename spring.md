## 1 - IoC-контейнер, бины, скоупы, жизненный цикл
Ожидание: могу объяснить, что происходит от старта приложения до готового бина, и почему на проекте лучше инжектить только через конструктор (а не через @Autowired)

### IoC контейнер (Inversion of Control)
**Inversion of Control** — spring отвечает за создание объектов и их связи. Мы не пишем 
`new OrderService(new OrderRepository(...))` — Spring сам создаёт
объекты (бины) и связывает их между собой.

**IoC-контейнер** в Spring — это `ApplicationContext` (расширение `BeanFactory`). Он:
- находит описания бинов (component scan, `@Configuration`-классы) и превращает их в `BeanDefinition`;
- создаёт бины, внедряет в них зависимости, вызывает callback-и жизненного цикла;
- хранит singleton-бины и отдаёт их по запросу;
- уничтожает бины при закрытии контекста.

**DI (Dependency Injection)** — конкретный способ реализации IoC: зависимости передаются объекту извне.
Варианты внедрения:
1. **Через конструктор** (рекомендуемый):
   ```java
   @Service
   public class OrderService {
       private final OrderRepository repository;

       public OrderService(OrderRepository repository) { // @Autowired не нужен
           this.repository = repository;
       }
   }
   ```
   Если конструктор один — `@Autowired` можно не ставить. Если конструкторов несколько —
   `@Autowired` ставится над тем, который Spring должен использовать
2. **Через сеттер** (`@Autowired` над сеттером) — для опциональных зависимостей, которые можно поменять после создания.
3. **Через поле** (`@Autowired` над полем) — не рекомендуется.

**Почему конструктор лучше, чем `@Autowired` над полем:**
- **Неизменяемость** — поля можно сделать `final`
- **Явные и обязательные зависимости** — можно в поле оставить `null`, и NPE вылезет только в рантайме
- **Тестируемость** — в unit-тесте просто `new OrderService(mockRepository)`, без Spring и без рефлексии
  (`ReflectionTestUtils`, `@InjectMocks`).
- **Нет скрытой связи с Spring** — можно использовать вне контейнера
- **Видна «перегрузка» класса** — если в конструкторе 8 параметров, это сигнал, что класс нарушает SRP.
- **Циклические зависимости обнаруживаются сразу** — при старте приложения падает
  `BeanCurrentlyInCreationException`, а не делает вид что все ок

### Бины
**Бин** — объект, который создаётся, настраивается и управляется IoC-контейнером.

Способы объявить бин:
1. **Аннотация `@Component` над классом** (или её alias: `@Service`, `@Repository`, `@Controller`,
   `@RestController`, `@Configuration`). Такие классы находит **component scan** (`@ComponentScan`,
   в Spring Boot включён через `@SpringBootApplication` — сканируется пакет главного класса и вложенные)
2. **Метод с `@Bean` в классе `@Configuration`** — метод является **factory method**. Используется, когда
   класс чужой (из библиотеки, нельзя повесить `@Component`) или нужна нетривиальная настройка:
   ```java
   @Configuration
   public class JacksonConfig {
       @Bean
       public ObjectMapper objectMapper() {
           return new ObjectMapper().findAndRegisterModules();
       }
   }
   ```
3. Программно — через `BeanDefinitionRegistry` / `registerBean(...)` (редко, в основном в стартерах).

**Имя бина** по умолчанию: для `@Component` — имя класса с маленькой буквы (`orderService`),
для `@Bean` — имя метода.

**Если подходящих бинов несколько** (например, две реализации интерфейса):
- `@Primary` — бин по умолчанию;
- `@Qualifier("name")` — явно указать нужный;
- можно внедрить все сразу: `List<PaymentProvider>` или `Map<String, PaymentProvider>` (ключ — имя бина).

### Scope
**Scope** определяет, сколько экземпляров бина создаёт контейнер и как долго они живут.
Задаётся аннотацией `@Scope` над классом или `@Bean`-методом.

| Scope | Сколько экземпляров | Где доступен |
|---|---|---|
| `singleton` (по умолчанию) | один на весь `ApplicationContext` | везде |
| `prototype` | новый при каждом запросе бина из контейнера | везде |
| `request` | один на HTTP-запрос | только web |
| `session` | один на HTTP-сессию | только web |
| `application` | один на `ServletContext` | только web |
| `websocket` | один на WebSocket-сессию | только web (WebSocket) |

### Жизненный цикл

ПОПРОБУЙ НАПИСАТЬ САМОСТОЯТЕЛЬНО

### 2 - Spring Boot auto-configuration
Ожидание: могу рассказать, как Boot находит автоконфигурации (AutoConfiguration.imports), и что делают @ConditionalOnClass, @ConditionalOnMissingBean, @ConditionalOnProperty.
### 3 - Spring MVC: DispatcherServlet, @RestController, HttpMessageConverter, @ControllerAdvice
Ожидание: могу назвать порядок приоритета (env-переменная / аргумент / application.yml / дефолт) и объяснить, как MY_SERVICE_URL попадает в поле serviceUrl.
### 4 - @Transactional: прокси, self-invocation, propagation
Ожидание: могу объяснить, почему вызов this.method() не открывает транзакцию, и когда нужен REQUIRES_NEW.
### 5 - Annotation processing
Ожидание: понимаю, что Lombok, MapStruct и async-client-lib работают на этапе компиляции, и знаю, где искать сгенерированный код.

### 6 - Написать свой мини-стартер (Опционально)
Ожидание: модуль с @AutoConfiguration, @ConfigurationProperties, @ConditionalOnMissingBean и AutoConfiguration.imports; подключил его в тестовое приложение и показал, что мой бин переопределяется бином пользователя. Принимается модуль-стартер (api, например)

### 7 - Могу объяснить, почему ObjectMapper в сервисе платформы отличается от дефолтного Spring Boot - ещё не зная кода платформы.

