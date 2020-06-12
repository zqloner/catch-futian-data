package com.mgl.controller.carshop;


import com.mgl.bean.carshop.CarNumberDict;
import com.mgl.bean.carshop.MglCarshopFutianDataDetail;
import com.mgl.bean.carshop.MglCarshopFutianVehicleFiles;
import com.mgl.service.carshop.CarNumberDictService;
import com.mgl.service.carshop.MglCarshopFutianVehicleFilesService;
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
 * 车辆档案总表 前端控制器
 * </p>
 *
 * @author zhangqi
 * @since 2020-06-11
 */
@Controller
@RequestMapping("/mglCarshopFutianVehicleFiles")
public class MglCarshopFutianVehicleFilesController {

    @Resource
    private MglCarshopFutianVehicleFilesService mglCarshopFutianVehicleFilesService;

    @PostMapping("/import")
    @ResponseBody
    public CommonResult upload(MultipartFile file) {
//        public static <T> List<T> readExcel(String path, Class<T> cls, MultipartFile file)
        String path = "";
        List<MglCarshopFutianVehicleFiles> list = ExcelUtils.readExcel(path, MglCarshopFutianVehicleFiles.class, file,0);
//        for (int i = 0; i < list.size(); i++) {
//            for  ( int  j  =  list.size()  -   1 ; j  >  i; j -- )  {
//                if  (list.get(j).getCarVin().equals(list.get(i).getCarVin()))  {
//                    return CommonResult.failed("第"+(i+2)+"行和第"+(j+2)+"行vin码重复,请核查!");
//                }
//            }
//            String carVin = list.get(i).getCarVin();
//            CarNumberDict dict = carNumberDictService.findByCarVin(carVin);
//            if (dict != null) {
//                return CommonResult.failed("第" + (i + 2) + "行错误，汽车vin号已经存在");
//            }
//            if (carVin.length()!=17) {
//                return CommonResult.failed("第" + (i + 2) + "行错误，vin码位数不正确");
//            }
//        }
        list.stream().forEach(x -> {
            x.setDelflag(0);
        });
        return mglCarshopFutianVehicleFilesService.saveBatch(list) ? CommonResult.success("添加成功！共导入" + list.size() + "条数据") : CommonResult.failed("导入失败");
    }

    /**
     * 每20万条数据放一个sheet
     * @param response
     */
    @RequestMapping("/exportExcel")
    public void exportFutianDataSXSSF(HttpServletResponse response) {
        List<MglCarshopFutianVehicleFiles> list = mglCarshopFutianVehicleFilesService.list(null);
        ExcelUtils.createExcel(response, list, MglCarshopFutianVehicleFiles.class, "futian_car_taotal_table.xlsx");
    }

}
