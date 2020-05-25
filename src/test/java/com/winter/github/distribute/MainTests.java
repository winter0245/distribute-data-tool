package com.winter.github.distribute;

import com.google.common.collect.Lists;
import com.winter.github.distribute.converter.AbstractBizConverter;
import com.winter.github.distribute.converters.ProductBizConverter;
import com.winter.github.distribute.converters.UserBizConverter;
import com.winter.github.distribute.model.OrderModel;
import com.winter.github.distribute.model.ProductModel;
import com.winter.github.distribute.model.UserInfoModel;
import com.winter.github.distribute.service.OrderService;
import com.winter.github.distribute.service.UserService;
import com.winter.github.distribute.utils.ReflectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <br>
 *
 * @author zhangdongdong<br>
 * @version 1.0<br>
 * @taskId <br>
 * @date 2020年03月30日 16:11:34 <br>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestServer.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Slf4j
public class MainTests {
    /**
     * 模拟初始化数据，结合spring的话可以用下面方式注入
     *
     * @Resource private List<AbstractBizConverter<?, ?>> converters;
     **/
    private List<AbstractBizConverter<?, ?>> converters = Lists.newArrayList();

    @Resource
    private UserService userService;

    @Resource
    private OrderService orderService;

    @Before
    public void init() {
        converters.add(new UserBizConverter());
        converters.add(new ProductBizConverter());
    }

    @Test
    public void testReflect() {
        PropertyDescriptor propertyMethod = ReflectUtil.getBeanPropertyMethod(UserInfoModel.class, "name");
        log.info("get property {} descriptor {}", "name", propertyMethod);
        Assert.assertNotNull(propertyMethod);
        propertyMethod = ReflectUtil.getBeanPropertyMethod(UserInfoModel.class, "name11");
        Assert.assertNull(propertyMethod);
    }

    @Test
    public void testConvert() {
        for (int index = 0; index < 100; index++) {
            AtomicInteger counter = new AtomicInteger();
            List<OrderModel> orders = Stream
                    .generate(() -> new OrderModel("order-" + counter.incrementAndGet(), RandomStringUtils.randomAlphabetic(5),
                            Lists.newArrayList("p-" + RandomStringUtils.randomAlphabetic(6), "p-" + RandomStringUtils.randomAlphabetic(6)))).limit(10)
                    .collect(
                            Collectors.toList());
            ReflectUtil.parallelConvert(converters, orders, OrderModel.class);
            orders.forEach(o -> {
                UserInfoModel userInfoModel = o.getUserInfoModel();
                Assert.assertNotNull(userInfoModel);
                Assert.assertEquals("user-" + o.getUserId(), userInfoModel.getName());
                List<ProductModel> productModels = o.getProductModels();
                Assert.assertEquals(productModels.size(), o.getProductIds().size());
                for (int i = 0; i < productModels.size(); i++) {
                    Assert.assertEquals("product-" + o.getProductIds().get(i), productModels.get(i).getName());
                }
                log.info("order convert result :{}", o);
            });
        }
    }

    @Test
    public void testUser() {
//        List<UserInfoModel> users = userService.getUsers();
//        Assert.assertFalse(users.isEmpty());
        for (int i = 0; i < 100; i++) {
            long start = System.currentTimeMillis();
            List<OrderModel> orderModels = orderService.queryOrders();
            Assert.assertFalse(orderModels.isEmpty());
            orderModels.forEach(o -> {
                Assert.assertNotNull(o.getUserInfoModel());
                Assert.assertFalse(o.getUserInfoModel().getAddressModels().isEmpty());
                Assert.assertFalse(o.getProductModels().isEmpty());
            });
            log.info("query  orders [{}]  success ,take time {}ms", orderModels.size(), System.currentTimeMillis() - start);
        }

    }

    public static void main(String[] args) {
        System.out.println(2 ^ 3);
    }
}
