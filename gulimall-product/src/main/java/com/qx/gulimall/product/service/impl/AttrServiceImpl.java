package com.qx.gulimall.product.service.impl;

import com.qx.common.constant.ProductConstant;
import com.qx.common.dto.es.SkuESModel;
import com.qx.gulimall.product.entity.*;
import com.qx.gulimall.product.entity.vo.AttrResVo;
import com.qx.gulimall.product.entity.vo.AttrVo;
import com.qx.gulimall.product.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qx.common.utils.PageUtils;
import com.qx.common.utils.Query;

import com.qx.gulimall.product.dao.AttrDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;
    @Autowired
    private SpuInfoService spuInfoService;
    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void saveAttrVo(AttrVo attr) {

        // 复制属性
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        this.save(attrEntity);

        // 保存冗余字段
        Long attrGroupId = attr.getAttrGroupId();
        Long attrId = attrEntity.getAttrId();
        AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        relationEntity.setAttrGroupId(attrGroupId);
        relationEntity.setAttrId(attrId);

        attrAttrgroupRelationService.save(relationEntity);
    }

    @Override
    public AttrResVo getInfoById(Long attrId) {
        // 查询基本参数
        AttrEntity attrEntity = this.getById(attrId);
        AttrResVo attrVo = new AttrResVo();
        // 复制属性
        if(attrEntity != null){
            BeanUtils.copyProperties(attrEntity,attrVo);
            // 通过catelogId查询catelog
            Long catelogId = attrEntity.getCatelogId();
            CategoryEntity categoryEntity = categoryService.getById(catelogId);
            String categoryName = null;
            if(categoryEntity != null){
                categoryName = categoryEntity.getName();
                attrVo.setAttrGroupName(categoryName);
            }

            /*// 通过关联信息查询分组
            AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
            String attrGroupName = null;
            if(relationEntity != null){
                Long attrGroupId = relationEntity.getAttrGroupId();
                AttrGroupEntity groupEntity = attrGroupService.getById(attrGroupId);
                attrGroupName = groupEntity.getAttrGroupName();
                attrVo.setAttrGroupId(attrEntity.getAttrId());
                attrVo.setAttrGroupName(attrGroupName);
            }*/
            // 查出关联中间表--》attrGroupId
            ArrayList<String> attrGroupNameList = getAttrGroupNameList(attrEntity);
            attrVo.setAttrGroupNameList(attrGroupNameList);

            // 设置catlogPath路径
            Long[] catlogPath = categoryService.getCatlogPath(catelogId);
            attrVo.setCatelogPath(catlogPath);
        }

        return attrVo;
    }

    @Override
    public PageUtils queryTypePage(Map<String, Object> params, Long catelogId, String type) {

        // 创建包装类对象 attr_type:0-销售属性，1-基本属性
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>().
                eq("attr_type",
                        type.equalsIgnoreCase("base")? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() :ProductConstant.AttrEnum.ATTR_TYPE_SALE.getMsg());

        // 判断catelogId是否为0
        if(catelogId != 0){
            wrapper.eq("catelog_id",catelogId);
        }

        // 封装参数
        if(params != null && params.size() > 0){
            // 检索关键字
            Object key = params.get("key");
            if(!StringUtils.isEmpty(key)){
                wrapper.and(queryWrapper -> {
                    // 拼接条件
                    queryWrapper.eq("attr_id",key).or().like("attr_name",key);
                });
            }
        }

        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                wrapper
        );
        if(page.getSize() != 0){
            System.out.println(page);
            PageUtils pageUtils = new PageUtils(page);
            List<AttrResVo> records =
                    // 使用流式转换重新封装数据
                    page.getRecords().stream()
                            .map(
                                    (attrEntity) -> {
                                        // 复制属性
                                        AttrResVo attrResVo = new AttrResVo();
                                        BeanUtils.copyProperties(attrEntity, attrResVo);
                                        // 查询catelogName和groupName
                                        String catelogName = null;
                                        Long catelogIdChild = attrEntity.getCatelogId();
                                        if (catelogIdChild != 0) {
                                            CategoryEntity categoryEntity = categoryService.getById(catelogIdChild);
                                            catelogName = categoryEntity.getName();
                                        }

                                        // 查出关联中间表--》attrGroupId
                                        List<AttrAttrgroupRelationEntity> relationList =
                                                attrAttrgroupRelationService.list(
                                                        new QueryWrapper<AttrAttrgroupRelationEntity>()
                                                                .eq("attr_id", attrEntity.getAttrId()));

                                        // 定义多重分组
                                        ArrayList<String> attrGroupNameList = getAttrGroupNameList(attrEntity);
                                        attrResVo.setCatelogName(catelogName);
                                        // 将多重分组添加到集合中
                                        attrResVo.setAttrGroupNameList(attrGroupNameList);

                      /*// 查出关联中间表--》attrGroupId
                      AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationService.getOne(
                              new QueryWrapper<AttrAttrgroupRelationEntity>()
                                      .eq("attr_id", attrEntity.getAttrId()));
                      if(relationEntity != null){
                          Long attrGroupId = relationEntity.getAttrGroupId();
                          // 通过attrGroupId查询attrgroup
                          AttrGroupEntity groupEntity = attrGroupService.getById(attrGroupId);
                          String attrGroupName = groupEntity.getAttrGroupName();
                          // 设置catelogName和groupName
                          attrResVo.setAttrGroupName(attrGroupName);

                      }
                      attrResVo.setCatelogName(catelogName);*/

                                        return attrResVo;
                                    })
                            .collect(Collectors.toList());
            // 重新设置records数据
            pageUtils.setList(records);
            return pageUtils;
        } else{
            return null;
        }
    }

    /**
     * 更新属性
     * @param attrResVo
     */
    @Override
    public void updateAttr(AttrResVo attrResVo) {


    }

    @Override
    public List<ProductAttrValueEntity> listforspuBySpuId(Long spuId) {

        // 通过spuId获取所有attrId
        List<ProductAttrValueEntity> productAttrValueList = productAttrValueService.list(
                new QueryWrapper<ProductAttrValueEntity>()
                        .eq("spu_id", spuId));

        return productAttrValueList;
    }

    @Override
    public List<SkuESModel.Attr> getSkuAttrByCatalogId(Long catalogId) {
        // 设置条件
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>().eq("catelog_id",catalogId);
        List<AttrEntity> attrEntities = this.list(wrapper);
        // 通过流形势组装数据并返回
        List<SkuESModel.Attr> attrList = attrEntities.stream().map(attrEntity -> {
            SkuESModel.Attr attr = new SkuESModel.Attr();
            // 复制属性
            BeanUtils.copyProperties(attrEntity, attr);

            return attr;
        }).collect(Collectors.toList());

        return attrList;
    }

    @Override
    public List<Long> queryAttrIdsIsSearch(List<Long> attrIds) {

        return baseMapper.selectAttrIdsIsSearch(attrIds);
    }

    @Override
    public PageUtils querySalePage(Map<String, Object> params, Long catelogId) {
        // 创建包装类对象//  attr_type:0-销售属性，1-基本属性
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>().eq("attr_type",0);

        // 判断catelogId是否为0
        if(catelogId != 0){
            wrapper.eq("catelog_id",catelogId);
        }

        // 封装参数
        if(params != null && params.size() > 0){
            // 检索关键字
            Object key = params.get("key");
            if(!StringUtils.isEmpty(key)){
                wrapper.and(queryWrapper -> {
                    queryWrapper.eq("attr_type",0).eq("attr_id",key).or().like("attr_name",key);
                });
            }
        }

        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                wrapper
        );
        if(page.getSize() != 0){
            System.out.println(page);
            PageUtils pageUtils = new PageUtils(page);
            List<AttrResVo> records =
                    // 使用流式转换重新封装数据
                    page.getRecords().stream().map((attrEntity) -> {
                        // 复制属性
                        AttrResVo attrResVo = new AttrResVo();
                        BeanUtils.copyProperties(attrEntity,attrResVo);
                        // 查询catelogName和groupName
                        String catelogName = null;
                        Long catelogIdChild = attrEntity.getCatelogId();
                        if(catelogIdChild != 0){
                            CategoryEntity categoryEntity = categoryService.getById(catelogIdChild);
                            catelogName = categoryEntity.getName();
                        }

                        // 查出关联中间表--》attrGroupId
                        AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationService.getOne(
                                new QueryWrapper<AttrAttrgroupRelationEntity>()
                                        .eq("attr_id", attrEntity.getAttrId()));
                        if(relationEntity != null){
                            Long attrGroupId = relationEntity.getAttrGroupId();
                            // 通过attrGroupId查询attrgroup
                            AttrGroupEntity groupEntity = attrGroupService.getById(attrGroupId);
                            String attrGroupName = groupEntity.getAttrGroupName();
                            // 设置catelogName和groupName
                            attrResVo.setAttrGroupName(attrGroupName);

                        }
                        attrResVo.setCatelogName(catelogName);
                        return attrResVo;
                    })
                            .collect(Collectors.toList());
            // 重新设置records数据
            pageUtils.setList(records);
            return pageUtils;
        } else{
            return null;
        }
    }

    @Override
    public PageUtils queryBasePage(Map<String, Object> params, Long catelogId) {
        // 创建包装类对象 attr_type:0-销售属性，1-基本属性
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>().eq("attr_type",1);

        // 判断catelogId是否为0
        if(catelogId != 0){
            wrapper.eq("catelog_id",catelogId);
        }

        // 封装参数
        if(params != null && params.size() > 0){
            // 检索关键字
            Object key = params.get("key");
            if(!StringUtils.isEmpty(key)){
                wrapper.and(queryWrapper -> {
                    // 拼接条件
                    queryWrapper.eq("attr_id",key).or().like("attr_name",key);
                });
            }
        }

        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                wrapper
        );
        if(page.getSize() != 0){
            System.out.println(page);
            PageUtils pageUtils = new PageUtils(page);
            List<AttrResVo> records =
                    // 使用流式转换重新封装数据
                    page.getRecords().stream()
                            .map(
                                    (attrEntity) -> {
                                        // 复制属性
                                        AttrResVo attrResVo = new AttrResVo();
                                        BeanUtils.copyProperties(attrEntity, attrResVo);
                                        // 查询catelogName和groupName
                                        String catelogName = null;
                                        Long catelogIdChild = attrEntity.getCatelogId();
                                        if (catelogIdChild != 0) {
                                            CategoryEntity categoryEntity = categoryService.getById(catelogIdChild);
                                            catelogName = categoryEntity.getName();
                                        }

                                        // 查出关联中间表--》attrGroupId
                                        List<AttrAttrgroupRelationEntity> relationList =
                                                attrAttrgroupRelationService.list(
                                                        new QueryWrapper<AttrAttrgroupRelationEntity>()
                                                                .eq("attr_id", attrEntity.getAttrId()));

                                        // 定义多重分组
                                        ArrayList<String> attrGroupNameList = getAttrGroupNameList(attrEntity);
                                        attrResVo.setCatelogName(catelogName);
                                        // 将多重分组添加到集合中
                                        attrResVo.setAttrGroupNameList(attrGroupNameList);

                      /*// 查出关联中间表--》attrGroupId
                      AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationService.getOne(
                              new QueryWrapper<AttrAttrgroupRelationEntity>()
                                      .eq("attr_id", attrEntity.getAttrId()));
                      if(relationEntity != null){
                          Long attrGroupId = relationEntity.getAttrGroupId();
                          // 通过attrGroupId查询attrgroup
                          AttrGroupEntity groupEntity = attrGroupService.getById(attrGroupId);
                          String attrGroupName = groupEntity.getAttrGroupName();
                          // 设置catelogName和groupName
                          attrResVo.setAttrGroupName(attrGroupName);

                      }
                      attrResVo.setCatelogName(catelogName);*/

                                        return attrResVo;
                                    })
                            .collect(Collectors.toList());
            // 重新设置records数据
            pageUtils.setList(records);
            return pageUtils;
        } else{
            return null;
        }

    }


    private ArrayList<String> getAttrGroupNameList(AttrEntity attrEntity){
        List<AttrAttrgroupRelationEntity> relationList =
                attrAttrgroupRelationService.list(
                        new QueryWrapper<AttrAttrgroupRelationEntity>()
                                .eq("attr_id", attrEntity.getAttrId()));

        // 定义多重分组
        ArrayList<String> attrGroupNameList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(relationList)){
            for (AttrAttrgroupRelationEntity relationEntity : relationList) {
                if (relationEntity != null) {
                    Long attrGroupId = relationEntity.getAttrGroupId();
                    // 通过attrGroupId查询attrgroup
                    AttrGroupEntity groupEntity = attrGroupService.getById(attrGroupId);
                    if(groupEntity != null){
                        String attrGroupName = groupEntity.getAttrGroupName();
                        // 设置groupName
                        attrGroupNameList.add(attrGroupName);
                    }
                }
            }
        }
        return attrGroupNameList;
    }
}