package com.mgl.utils.sdk.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description TODO
 * @Author fengwei
 * @Date 2020/7/23 9:15
 * @Version 1.0
 */
public class GroupUtil {

    /**
     * 对list进行分组
     * @param list 带分组的list
     * @param groupCount 每个分组的数据条数
     * @return 分组后的list
     */
    public static<T> List<List<T>> divideGroup(List<T> list, int groupCount){
        if (list.size() == 0) {
            return null;
        }
        // 本次分组的总个数
        int totalCount = list.size();
        // 进行分发
        List<List<T>> groupList = new ArrayList<>();
        // 如果取到的数据比分组数据还小，则不用分组
        if (totalCount < groupCount) {
            groupList.add(list);
        } else {
            // 分组数
            int remainder = totalCount % groupCount;
            int groupNum = totalCount / groupCount ;
            // 有余数
            if (remainder > 0) {
                groupNum += 1;
            }
            for (int i = 0 ; i < groupNum ; i++) {
                if (remainder > 0 && i == groupNum -1) {
                    groupList.add(list.subList(i * groupCount, i * groupCount + remainder ));
                } else {
                    groupList.add(list.subList(i * groupCount, (i + 1) * groupCount));
                }

            }

        }
        return groupList;
    }

    /**
     * 将集合按指定数量分组
     * @param list 数据集合
     * @param quantity 分组数量
     * @return 分组结果
     */
    public static <T> List<List<T>> groupListByQuantity(List<T> list, int quantity) {
        if (list == null || list.size() == 0) {
            return null;
        }

        if (quantity <= 0) {
            new IllegalArgumentException("Wrong quantity.");
        }

        List<List<T>> wrapList = new ArrayList<List<T>>();
        int count = 0;
        while (count < list.size()) {
            wrapList.add(new ArrayList<T>(list.subList(count, (count + quantity) > list.size() ? list.size() : count + quantity)));
            count += quantity;
        }

        return wrapList;
    }


    /**
     * 按list中某个值进行分组
     * @param list 待分组的list
     * @param <T> 泛型
     * @return 分组后的list
     */
    public static<T> List<List<T>> csvGroup(List<T> list){
        return null;
    }
}
