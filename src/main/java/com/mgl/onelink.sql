/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.0.111(root)
 Source Server Type    : MySQL
 Source Server Version : 50730
 Source Host           : 192.168.0.111:3306
 Source Schema         : onelink

 Target Server Type    : MySQL
 Target Server Version : 50730
 File Encoding         : 65001

 Date: 17/12/2021 11:16:56
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for car_golden_dragon_number_dict
-- ----------------------------
DROP TABLE IF EXISTS `car_golden_dragon_number_dict`;
CREATE TABLE `car_golden_dragon_number_dict`  (
  `car_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增长主键',
  `car_num` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '车牌号',
  `car_no` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '车号',
  `car_terminal_num` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '终端编号',
  `car_vin` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'VIN',
  `car_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '车型',
  `car_mileage` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '里程(公里）',
  `car_customer_client` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订单客户',
  `car_report_date` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上报时间',
  `car_final_location` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '最后位置',
  `car_flag` int(1) UNSIGNED ZEROFILL NULL DEFAULT 0 COMMENT '软删除',
  PRIMARY KEY (`car_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1510 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for car_number_dict
-- ----------------------------
DROP TABLE IF EXISTS `car_number_dict`;
CREATE TABLE `car_number_dict`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `car_vin` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'VIN',
  `car_number` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '车牌号',
  `province` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '惠州市',
  `county_info` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '区县',
  `vehicle_customer` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '整车客户',
  `battery_number` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '电池编号',
  `order_number` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订单号',
  `battery_type` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '电池类型',
  `single_capacity` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '单体容量',
  `now_bcu_software_version` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '现BCU软件版本',
  `original_bcu_software_version` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '原BCU软件版本',
  `bus_route` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公交线路',
  `bus_company` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公交公司',
  `vehicle_service_time` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '车辆投入使用时间',
  `mixed_model` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '混合模式',
  `main_turning_number` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '主机厂车工号',
  `galvanic_cell_number` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '原电池编号',
  `note` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `del_flag` int(5) NULL DEFAULT NULL COMMENT '删除标识符',
  `group_lable` int(1) NULL DEFAULT NULL COMMENT '分组标识符',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3952 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for golden_dragon
-- ----------------------------
DROP TABLE IF EXISTS `golden_dragon`;
CREATE TABLE `golden_dragon`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `vin` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '车辆VIN',
  `terminal_number` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '终端编号',
  `online` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '在线状态(1为在线,0为不在线)',
  `result` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应答结果(1为成功)',
  `total_current` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '总电流   329610',
  `total_voltage` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '总电压   329609',
  `soc` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'SOC   329611',
  `params_first` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '最高电压电池子系统号    329622',
  `params_second` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '最高电压电池单体代号   329623',
  `params_third` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '电池单体电压最高值  329624',
  `params_fouth` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '最低电压电池子系统号   329625',
  `params_fiveth` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '最低电压电池单体代号    329626',
  `params_six` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '电池单体电压最低值   329627',
  `params_seven` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '最高温度子系统号  329628',
  `params_eight` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '最高温度探针序号   329629',
  `params_ten` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '最高温度值   329630',
  `params_eleven` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '最低温度子系统号   329631',
  `params_tewlve` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '最低温度探针序号   329632',
  `params_thirteen` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '最低温度值     329633',
  `params_fourteen` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '通用报警标志    329635',
  `ceate_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `data_current_time` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '数据时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `vin_index`(`vin`) USING BTREE,
  INDEX `data_current_time_index`(`data_current_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 61345219 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '金龙数据' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mgl_carshop_futian_vehicle_files
-- ----------------------------
DROP TABLE IF EXISTS `mgl_carshop_futian_vehicle_files`;
CREATE TABLE `mgl_carshop_futian_vehicle_files`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `province` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '市级',
  `county_info` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '区/县',
  `car_factory` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '整车厂',
  `battery_type` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '电池类型',
  `capacity` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '容量',
  `battery_system` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '电池系统',
  `parallel_connection` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '并联',
  `series_connection` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '串联',
  `vehicle_system` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '整车系统',
  `construction` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '结构（本项内容为开口填充项）',
  `vehicle_configuration` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '整车配置，尽量补充',
  `order_number` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订单号',
  `number` int(11) NULL DEFAULT NULL COMMENT '数量',
  `run_date` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '运行日期(严格按照格式:1970-01-01)',
  `warranty_period` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '质保期限（年/公里）',
  `status` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否\r\n过保/报废',
  `duty_engineer` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '责任工程师',
  `service_station` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '覆盖授权服务站',
  `note` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `three_may_kilometers` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '万公里\r\n2020年3月',
  `two_second_kilometers` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '万公里2020年2月',
  `question` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '问题',
  `ke_poor` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公里差',
  `delflag` int(5) NULL DEFAULT NULL COMMENT '删除标识符',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '车辆档案总表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
