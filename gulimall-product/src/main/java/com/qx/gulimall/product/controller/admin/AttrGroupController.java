package com.qx.gulimall.product.controller.admin;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qx.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.qx.gulimall.product.entity.AttrEntity;
import com.qx.gulimall.product.entity.vo.AttGroupWithAttrVo;
import com.qx.gulimall.product.service.AttrAttrgroupRelationService;
import com.qx.gulimall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.qx.gulimall.product.entity.AttrGroupEntity;
import com.qx.gulimall.product.service.AttrGroupService;
import com.qx.common.utils.PageUtils;
import com.qx.common.utils.R;



/**
 * 属性分组
 *
 * @author qx
 * @email 1375257663@qq.com
 * @date 2020-07-06 16:57:26
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    /**
     * 获取指定分组关联的所有属性
     * @param attrgroupId
     * @return
     */
    @GetMapping("{attrgroupId}/attr/relation")
    public R getAllRelation(@PathVariable Long attrgroupId){

        List<AttrEntity> attrEntityList = attrGroupService.getRelationAttr(attrgroupId);

        return R.ok().put("data",attrEntityList);
    }

    /**
     * 获取属性分组里面还没有关联的本分类里面的其他基本属性，方便添加新的关联  1/noattr/relation
     * @param attrgroupId
     * @return
     */
    @GetMapping("{attrgroupId}/noattr/relation")
    public R getNoattrRelation(@RequestParam Map<String,Object> params,
                               @PathVariable Long attrgroupId){

        PageUtils page = attrGroupService.getNoattrRelation(params,attrgroupId);

        return R.ok().put("page",page);
    }


    /**
     * 获取分类下所有分组&关联属性
     * @param catelogId
     * @return
     */
    @GetMapping("/{catelogId}/withattr")
    public R getGroupWithattr(@PathVariable Long catelogId){

        List<AttGroupWithAttrVo> voList = attrGroupService.getGroupWithAttr(catelogId);

        return R.ok().put("data",voList);
    }


    /**
     * 列表
     */
    @GetMapping("/list/{catlogId}")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable Long catlogId){
        PageUtils page = attrGroupService.queryPage(params,catlogId);

        if(page == null){
            return R.error();
        } else{
            return R.ok().put("page", page);
        }
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        // 获取当前id，查询
        Long catelogId = attrGroup.getCatelogId();

        Long[] catlogPath = attrGroup.getCatelogPath();
        catlogPath = categoryService.getCatlogPath(catelogId);

        attrGroup.setCatelogPath(catlogPath);

        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 添加属性与分组关联关系
     */
    @PostMapping("/attr/relation")
    public R attrRelation(@RequestBody List<AttrAttrgroupRelationEntity> relationList){
		attrAttrgroupRelationService.saveBatch(relationList);

        return R.ok();
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("attr/relation/delete")
    public R relationDelete(@RequestBody List<AttrAttrgroupRelationEntity> relationList){

        /*QueryWrapper<AttrAttrgroupRelationEntity> queryWrapper = new QueryWrapper<>();
        if(relationList != null && relationList.size()>0){
            for (AttrAttrgroupRelationEntity relationEntity : relationList) {
                queryWrapper.eq("attr_id",relationEntity.getAttrId()).eq("attr_group_id",relationEntity.getAttrGroupId());
            }
            attrAttrgroupRelationService.remove(queryWrapper);
        }*/
        attrAttrgroupRelationService.batchRemove(relationList);
        return R.ok();
    }

}
