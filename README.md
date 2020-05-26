# distribute-data-tool
### 1.聚合数据(可以参考MainTests用例)

#### 1.1定义需要聚合的实体类，例如

```java
/**
 * 模拟订单对象<br>
 * @version 1.0<br>
 * @taskId <br>
 * @date 2020年03月30日 16:52:06 <br>
 */
@Data
public class OrderModel {

    private String id;


    private String userId;


    private List<String> productIds;

    /**
     * 用户信息
     */
    private UserInfoModel userInfoModel;

    /**
     * 商品信息
     */
    private List<ProductModel> productModels;

    public OrderModel(String id, String userId, List<String> productIds) {
        this.id = id;
        this.userId = userId;
        this.productIds = productIds;
    }
}
```

#### 1.2添加需要关联查询的字段，例如

```java
   	/**
   	* 转换器会将userId查询出的user对象注入到userInfoModel属性
   	*/
    @CombineField(value = Constants.USER_MODULE, convertField = "userInfoModel")
    private String userId;

	    /**
     * 用户信息
     */
    private UserInfoModel userInfoModel;
```

#### 1.3继承AbstractBizConverter，实现自定义的转换器

```java
/**
 * 模拟用户数据聚合转换<br>
 * @version 1.0<br>
 * @taskId <br>
 * @date 2020年03月30日 17:03:37 <br>
 */
public class UserBizConverter extends AbstractBizConverter<String, UserInfoModel> {
    @Override protected String getBizModule() {
        //指定的业务模块，需要与 @CombineField 注解中的value保持一致
        return Constants.USER_MODULE;
    }

    @Override protected Map<String, UserInfoModel> queryConvertDataByIds(Set<String> ids) {
        //实际场景中此处应该去数据库、nosql 或其他微服务根据id集合查询数据
        return ids.stream().collect(Collectors.toMap(id -> id, id -> new UserInfoModel(id, "user-" + id)));
    }

    @Override protected <R> void convertField(R row, Map.Entry<Field, CombineField> bizEntry, List<UserInfoModel> matchList) {
        //通过反射注入查询的结果到目标字段，此方法由子类实现的原因是某些场景可能不需要注入完整的对象，
        // 例如可能只需要用户的名称，所以需要子类自定义实现
        ReflectUtil.setPropertyValue(row, bizEntry.getValue().convertField(), matchList.get(0));
    }

}
```

#### 1.4批量执行聚合

```java
   
     /**
     * 初始化数据转换器，结合spring的话可以用下面方式注入
     *
     *  @Resource 
     *  private List<AbstractBizConverter<?, ?>> converters;
     **/
   private List<AbstractBizConverter<?, ?>> converters = Lists.newArrayList();

    @Before
    public void init() {
        converters.add(new UserBizConverter());
        converters.add(new ProductBizConverter());
    }
    //获取数据
	List<OrderModel> orders ;
    //聚合关联数据 
    ReflectUtil.parallelConvert(converters, orders, OrderModel.class);
```

#### 1.5利用注解聚合

给方法增加`@Combine`注解标记为需要聚合返回结果，可以省略 1.4 的步骤，如下。

```java
    @Override
    @Combine(OrderModel.class)
    public List<OrderModel> queryOrders() {
        return Stream.generate(() -> {
            OrderModel orderModel = new OrderModel();
            orderModel.setId(RandomStringUtils.randomAlphanumeric(6));
            orderModel.setProductIds(Lists.newArrayList(RandomStringUtils.randomAlphanumeric(6), RandomStringUtils.randomAlphanumeric(6)));
            orderModel.setUserId(UUID.randomUUID().toString());
            return orderModel;
        }).limit(10).collect(Collectors.toList());
    }
```

