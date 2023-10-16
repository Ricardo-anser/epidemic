package com.gougou.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gougou.common.result.Result;
import com.gougou.entity.Menu;
import com.gougou.service.IMenuService;
import com.gougou.utils.PermsDynamicUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author gougou
 * @since 2023-07-10
 */
@Slf4j
@RestController
@RequestMapping("/menu")
public class MenuController {
    @Autowired
    IMenuService menuService;
    @Autowired
    PermsDynamicUtils permsDynamicUtils;
    @GetMapping("/menulist")
    public Result getRoleList(@RequestParam Integer pageNum,
                              @RequestParam Integer pageSize,
                              @RequestParam String label,
                              @RequestParam String perms){
        log.info("页号+页面大小");
        log.info(String.valueOf(pageNum+pageSize));
        IPage<Menu> menuPage=new Page<>(pageNum,pageSize);
        QueryWrapper<Menu> queryWrapper=new QueryWrapper<>();
        if(StringUtils.isNotBlank(label)||StringUtils.isNotBlank(perms)){
            queryWrapper.and(wrapper->wrapper
                    .like(StringUtils.isNotBlank(label),"label",label)
                    .like(StringUtils.isNotBlank(perms),"perms",perms));
        }

        IPage<Menu> newMenuPage=menuService.page(menuPage,queryWrapper);
        Long total=menuService.count();
        List<Menu> menuList=newMenuPage.getRecords();
        Map<String,Object> data=new HashMap<>(16);
        data.put("total",total);
        data.put("menuList",menuList);
        return Result.success(data);
    }
    @GetMapping("/setStatus")
    public Result setStatus(@RequestParam Integer status,@RequestParam Long menuId){
        UpdateWrapper<Menu> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("menu_id",menuId);
        updateWrapper.set("status",status);
        menuService.update(null,updateWrapper);
        Map<String,Object> data= permsDynamicUtils.returnPackageTreeandPerms();
        return Result.success(data);
    }
    @PostMapping("/insertMenu")
    public Result insertMenu(@RequestBody Menu menu){
        menuService.saveOrUpdate(menu);
        Map<String,Object> data= permsDynamicUtils.returnPackageTreeandPerms();
        return Result.success(data);
    }

    /**
     * 菜单删除
     * 删除之后需要刷新菜单树信息，权限信息返回到前端，暂时搁置，
     * 注意父子菜单的删除逻辑(算了，前端改用tree-table)，到时候还要重写逻辑
     * @param menuId 待删除的菜单id
     * @return 返回相关信息
     */
    @DeleteMapping("/deleteMenu/{menuId}")
    public Result deleteMenu(@PathVariable Long menuId){
        menuService.removeById(menuId);
        Map<String,Object> data= permsDynamicUtils.returnPackageTreeandPerms();
        return Result.success(data);
    }
    @PostMapping("/deleteBatchMenu")
    public Result deleteBatchMenu(@RequestBody List<Long> menuIds){
        menuService.removeByIds(menuIds);
        Map<String,Object> data= permsDynamicUtils.returnPackageTreeandPerms();
        return Result.success(data);
    }
}
