package us.supercheng.mapper;

import us.supercheng.vo.CategoryHighlightVO;
import us.supercheng.vo.CategoryVO;
import java.util.List;

public interface CategoryMapperCustom {
    List<CategoryVO> getSubCatList(Integer parentId);
    CategoryHighlightVO getSixNewItems(Integer catId);
}