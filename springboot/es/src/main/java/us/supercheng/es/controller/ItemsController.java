package us.supercheng.es.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import us.supercheng.api.controller.BaseController;
import us.supercheng.es.service.impl.ItemsService;
import us.supercheng.utils.APIResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("es")
public class ItemsController extends BaseController {

    @Autowired
    private ItemsService itemsService;

    @GetMapping("hello")
    public APIResponse hello() {
        return APIResponse.ok();
    }

    @GetMapping("search")
    public APIResponse getItemsSearchResultsByKeywords(@RequestParam(required = false) String keywords,
                                             @RequestParam(required = false) String sort,
                                             @RequestParam(required = false) Integer page,
                                             @RequestParam(required = false) Integer pageSize) {
        if (StringUtils.isBlank(keywords))
            keywords = "";

        if (StringUtils.isBlank(sort))
            sort = "";

        if (page == null)
            page = DEFAULT_PAGE;
        page--;

        if (pageSize == null)
            pageSize = DEFAULT_PAGE_SIZE;

        Map<String, Object> map = new HashMap<>();
        map.put("keywords", keywords);
        map.put("sort", sort);

        return APIResponse.ok(itemsService.doSearchByKeywordsAndCatId(map, page, pageSize));
    }
}