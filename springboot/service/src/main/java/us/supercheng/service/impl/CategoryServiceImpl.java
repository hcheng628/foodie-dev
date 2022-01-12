package us.supercheng.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import us.supercheng.mapper.CategoryMapper;
import us.supercheng.mapper.CategoryMapperCustom;
import us.supercheng.pojo.Category;
import us.supercheng.service.CategoryService;
import us.supercheng.utils.JsonUtils;
import us.supercheng.utils.RedisOperator;
import us.supercheng.vo.CategoryHighlightVO;
import us.supercheng.vo.CategoryVO;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryMapperCustom categoryMapperCustom;

    @Autowired
    private RedisOperator redisOperator;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Category> getCategoriesByType(Integer typeId) {
        Example categoryExp = new Example(Category.class);
        Example.Criteria categoryCriteria = categoryExp.createCriteria();
        categoryCriteria.andEqualTo("type", typeId);
        return this.categoryMapper.selectByExample(categoryExp);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Category> getBaseCategories() {
        List<Category> ret = null;
        String res = this.redisOperator.get("index_base_categories");

        if (StringUtils.isBlank(res)) {
            ret = this.getCategoriesByType(CategoryService.CATEGORY_BASE_LEVEL);
            this.redisOperator.set("index_base_categories", JsonUtils.objectToJson(ret));
        } else
            ret = JsonUtils.jsonToList(res, Category.class);

        return ret;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<CategoryVO> getSubCatListByParentId(Integer parentId) {
        List<CategoryVO> ret = null;
        String key = "index_sub_cat:" + parentId,
               res = this.redisOperator.get(key);

        if (StringUtils.isBlank(res)) {
            ret = this.categoryMapperCustom.getSubCatList(parentId);
            this.redisOperator.set(key, JsonUtils.objectToJson(ret));
        } else
            ret = JsonUtils.jsonToList(res, CategoryVO.class);

        return ret;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public CategoryHighlightVO getSixNewItems(Integer catId) {
        return this.categoryMapperCustom.getSixNewItems(catId);
    }
}