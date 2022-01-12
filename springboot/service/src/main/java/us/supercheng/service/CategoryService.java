package us.supercheng.service;

import us.supercheng.pojo.Category;
import us.supercheng.vo.CategoryHighlightVO;
import us.supercheng.vo.CategoryVO;

import java.util.List;

public interface CategoryService {
    Integer CATEGORY_BASE_LEVEL = 1;

    List<Category> getCategoriesByType(Integer typeId);
    List<Category> getBaseCategories();
    List<CategoryVO> getSubCatListByParentId(Integer parentId);
    CategoryHighlightVO getSixNewItems(Integer catId);
}