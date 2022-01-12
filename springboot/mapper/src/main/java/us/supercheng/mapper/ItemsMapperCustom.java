package us.supercheng.mapper;

import org.apache.ibatis.annotations.Param;
import us.supercheng.pojo.ItemsImg;
import us.supercheng.vo.ItemCommentVO;
import us.supercheng.vo.ItemSearchResVO;
import us.supercheng.vo.ShopcartItemVO;
import java.util.List;
import java.util.Map;

public interface ItemsMapperCustom {
    List<ItemCommentVO> getCommentsByLevel(@Param("paraMap") Map<String, Object> map);
    List<ItemSearchResVO> doSearchByKeywordsAndCatId(@Param("paraMap") Map<String, Object> map);
    List<ShopcartItemVO> getItemsBySpecIds(@Param("ids") List<String> ids);
    List<ItemsImg> getItemsImgMainByItemId(@Param("itemId") String itemId);
    int updateItemsSpecCount(@Param(("paraMap")) Map<String, Object> map);
}