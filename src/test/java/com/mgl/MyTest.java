package com.mgl;

import com.mgl.bean.Car;
import com.mgl.utils.props.BeanAndMap;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Description:
 * @Author: zhangqi
 * @CreateTime: 2020/6/2311:39
 * @Company: MGL
 */
public class MyTest {
    public static void main(String[] args) throws Exception{
//        Car car = new Car();
//        car.setId("00000");
//        car.setColor("black");
//        car.setModel("bmw x5");
//        car.setVolume("3.0L");
//        car.setYear(2018);
//
//        System.out.println(BeanAndMap.beanToMap(car));
//        Map<String, Object> map = BeanAndMap.beanToMap(car);
//        Car car1 = new Car();
//        System.out.println(BeanAndMap.mapToBean(map,car1));
//        System.out.println(car1.getVolume());
        System.out.println(UUID.randomUUID().toString().replaceAll("-", ""));
    }
}
