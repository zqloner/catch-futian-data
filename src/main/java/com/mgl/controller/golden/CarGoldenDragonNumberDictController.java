package com.mgl.controller.golden;


import com.mgl.bean.golden.CarGoldenDragonNumberDict;
import com.mgl.common.Gloables;
import com.mgl.service.golden.CarGoldenDragonNumberDictService;
import com.mgl.utils.common.CommonResult;
import com.mgl.utils.excel.ExcelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author fengwei
 * @since 2020-07-22
 */
@Controller
@RequestMapping("/carGoldenDragonNumberDict")
public class CarGoldenDragonNumberDictController {

    @Autowired
    private CarGoldenDragonNumberDictService service;

    @PostMapping("/import")
    @ResponseBody
    public CommonResult upload(MultipartFile file) {
//        public static <T> List<T> readExcel(String path, Class<T> cls, MultipartFile file)
        String path = "";
        List<CarGoldenDragonNumberDict> list = ExcelUtils.readExcel(path, CarGoldenDragonNumberDict.class, file, 0);
        for (int i = 0; i < list.size(); i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).getCarVin().equals(list.get(i).getCarVin())) {
                    return CommonResult.failed("第" + (i + 2) + "行和第" + (j + 2) + "行vin码重复,请核查!");
                }
            }
            String carVin = list.get(i).getCarVin();
            CarGoldenDragonNumberDict dict = service.findByCarVin(carVin);
            if (dict != null) {
                return CommonResult.failed("第" + (i + 2) + "行错误，汽车vin号已经存在");
            }
            if (carVin.length() != 17) {
                return CommonResult.failed("第" + (i + 2) + "行错误，vin码位数不正确");
            }
        }
        list.stream().forEach(x -> {
            x.setCarFlag(Gloables.DELETE_FLAG);
        });
        return service.saveBatch(list) ? CommonResult.success("添加成功！共导入" + list.size() + "条数据") : CommonResult.failed("导入失败");
    }
}
