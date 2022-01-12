package us.supercheng.mapper;

import org.apache.ibatis.annotations.Param;
import us.supercheng.vo.MyCommentVO;
import java.util.List;
import java.util.Map;

public interface ItemsCommentsMapperCustom {
    void saveUserCommentList(@Param(("paraMap")) Map<String, Object> map);
    List<MyCommentVO> queryUserComments(@Param(("paraMap")) Map<String, Object> map);
}