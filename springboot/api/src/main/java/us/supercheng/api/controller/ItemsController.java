package us.supercheng.api.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import us.supercheng.pojo.Items;
import us.supercheng.pojo.ItemsImg;
import us.supercheng.pojo.ItemsParam;
import us.supercheng.pojo.ItemsSpec;
import us.supercheng.service.ItemsService;
import us.supercheng.utils.APIResponse;
import us.supercheng.vo.ItemsInfoVO;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("items")
public class ItemsController extends BaseController {

    @Autowired
    private ItemsService itemsService;


    @GetMapping("info/{id}")
    public APIResponse getItemDetailById(@PathVariable String id) {
        if (id == null)
            return APIResponse.errorMsg("Missing Item ID in request");

        Items item = this.itemsService.queryItemById(id);
        List<ItemsImg> imgs = this.itemsService.queryItemImgListByItemId(id);
        List<ItemsSpec> specs = this.itemsService.queryItemSpacListByItemId(id);
        ItemsParam param = this.itemsService.queryItemParamByItemId(id);

        ItemsInfoVO itemsInfoVO = new ItemsInfoVO();
        itemsInfoVO.setItem(item);
        itemsInfoVO.setItemImgList(imgs);
        itemsInfoVO.setItemSpecList(specs);
        itemsInfoVO.setItemParams(param);

        return APIResponse.ok(itemsInfoVO);
    }

    @GetMapping("commentLevel")
    public APIResponse getItemCommentsSummary(@RequestParam String itemId) {
        if (itemId == null)
            return APIResponse.errorMsg("Missing Item ID in request");

        return APIResponse.ok(this.itemsService.getItemCommentsSummary(itemId));
    }

    @GetMapping("comments")
    public APIResponse getItemCommentsSummary(@RequestParam String itemId,
                                              @RequestParam(required = false) Integer level,
                                              @RequestParam(required = false) Integer page,
                                              @RequestParam(required = false) Integer pageSize) {
        if (itemId == null)
            return APIResponse.errorMsg("Missing Item ID in request");

        Map<String, Object> map = new HashMap<>();
        map.put("id", itemId);

        if (level != null)
            map.put("level", level);

        if (page == null)
            page = DEFAULT_PAGE;

        if (pageSize == null)
            pageSize = DEFAULT_PAGE_SIZE;

        return APIResponse.ok(this.itemsService.getComments(map, page, pageSize));
    }


    @GetMapping("search")
    public APIResponse getItemsSearchResultsByKeywords(@RequestParam(required = false) String keywords,
                                             @RequestParam(required = false) String sort,
                                             @RequestParam(required = false) Integer page,
                                             @RequestParam(required = false) Integer pageSize) {
        if (keywords == null)
            keywords = "";
        else
            keywords = "%" + keywords + "%";

        if (sort == null)
            sort = "k";

        if (page == null)
            page = DEFAULT_PAGE;

        if (pageSize == null)
            pageSize = DEFAULT_PAGE_SIZE;

        Map<String, Object> map = new HashMap<>();
        map.put("keywords", keywords);
        map.put("sort", sort);

        return APIResponse.ok(this.itemsService.doSearchByKeywordsAndCatId(map, page, pageSize));
    }


    @GetMapping("/catItems")
    public APIResponse getItemsSearchResultsByCatId(@RequestParam(required = false) String catId,
                                             @RequestParam(required = false) String sort,
                                             @RequestParam(required = false) Integer page,
                                             @RequestParam(required = false) Integer pageSize) {
        if (catId == null)
            catId = "";

        if (sort == null)
            sort = "k";

        if (page == null)
            page = DEFAULT_PAGE;

        if (pageSize == null)
            pageSize = DEFAULT_PAGE_SIZE;

        Map<String, Object> map = new HashMap<>();
        map.put("catId", catId);
        map.put("sort", sort);

        return APIResponse.ok(this.itemsService.doSearchByKeywordsAndCatId(map, page, pageSize));
    }

    @GetMapping("refresh")
    public APIResponse shopcartItemRefresh(@RequestParam String itemSpecIds) {
        if (StringUtils.isBlank(itemSpecIds))
            return APIResponse.ok();

        String[] arr = itemSpecIds.split(",");
        return APIResponse.ok(this.itemsService.getItemsBySpecIds(Arrays.asList(arr)));
    }
}