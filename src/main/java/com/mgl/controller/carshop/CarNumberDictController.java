package com.mgl.controller.carshop;


import com.mgl.bean.carshop.CarNumberDict;
import com.mgl.utils.constants.Gloables;
import com.mgl.service.carshop.CarNumberDictService;
import com.mgl.utils.common.CommonResult;
import com.mgl.utils.excel.ExcelUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zhangqi
 * @since 2020-06-11
 */
@Controller
@RequestMapping("/carNumberDict")
public class CarNumberDictController {

    @Resource
    private CarNumberDictService carNumberDictService;

    @PostMapping("/import")
    @ResponseBody
    public CommonResult upload(MultipartFile file) {
//        public static <T> List<T> readExcel(String path, Class<T> cls, MultipartFile file)
        String path = "";
        List<CarNumberDict> list = ExcelUtils.readExcel(path, CarNumberDict.class, file, 0);
        for (int i = 0; i < list.size(); i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).getCarVin().equals(list.get(i).getCarVin())) {
                    return CommonResult.failed("第" + (i + 2) + "行和第" + (j + 2) + "行vin码重复,请核查!");
                }
            }
            String carVin = list.get(i).getCarVin();
            CarNumberDict dict = carNumberDictService.findByCarVin(carVin);
            if (dict != null) {
                return CommonResult.failed("第" + (i + 2) + "行错误，汽车vin号已经存在");
            }
            if (carVin.length() != 17) {
                return CommonResult.failed("第" + (i + 2) + "行错误，vin码位数不正确");
            }
        }
        list.stream().forEach(x -> {
            x.setDelFlag(Gloables.DELETE_NORMAL);
        });
        return carNumberDictService.saveBatch(list) ? CommonResult.success("添加成功！共导入" + list.size() + "条数据") : CommonResult.failed("导入失败");
    }


    /**
     * 导出
     *
     * @param response
     */
    @RequestMapping("/exportExcel")
    public void exportExcel(HttpServletResponse response) {
        List<CarNumberDict> list = carNumberDictService.list(null);
        ExcelUtils.createExcel(response, list, CarNumberDict.class, "cars-info.xlsx");
    }


    @RequestMapping("/getCarsInfo")
    @ResponseBody
    public CommonResult getCarsInfo() {
        return CommonResult.success(carNumberDictService.getCarsInfo());
    }
}
