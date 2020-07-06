package com.mgl.bean.golden;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 金龙数据实时抓取
 * </p>
 *
 * @author zhangqi
 * @since 2020-07-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class GoldenDragonTest implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 车辆VIN
     */
    private String vin;

    /**
     * 终端编号
     */
    private String terminalNumber;

    /**
     * 在线状态(1为在线,0为不在线)
     */
    private String online;

    /**
     * 应答结果(1为成功)
     */
    private String result;

    /**
     * 总电流   329610
     */
    private String totalCurrent;

    /**
     * 总电压   329609
     */
    private String totalVoltage;

    /**
     * SOC   329611
     */
    private String soc;

    /**
     * 最高电压电池子系统号    329622
     */
    private String paramsFirst;

    /**
     * 最高电压电池单体代号   329623
     */
    private String paramsSecond;

    /**
     * 电池单体电压最高值  329624
     */
    private String paramsThird;

    /**
     * 最低电压电池子系统号   329625
     */
    private String paramsFouth;

    /**
     * 最低电压电池单体代号    329626
     */
    private String paramsFiveth;

    /**
     * 电池单体电压最低值   329627
     */
    private String paramsSix;

    /**
     * 最高温度子系统号  329628
     */
    private String paramsSeven;

    /**
     * 最高温度探针序号   329629
     */
    private String paramsEight;

    /**
     * 最高温度值   329630
     */
    private String paramsTen;

    /**
     * 最低温度子系统号   329631
     */
    private String paramsEleven;

    /**
     * 最低温度探针序号   329632
     */
    private String paramsTewlve;

    /**
     * 最低温度值     329633
     */
    private String paramsThirteen;

    /**
     * 通用报警标志    329635
     */
    private String paramsFourteen;

    /**
     * 创建时间
     */
    private LocalDateTime ceateTime;

    /**
     * 数据时间
     */
    private String dataCurrentTime;


}
