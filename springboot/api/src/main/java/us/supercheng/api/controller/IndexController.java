package us.supercheng.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import us.supercheng.service.CarouselService;
import us.supercheng.service.CategoryService;
import us.supercheng.utils.APIResponse;

@RestController
@RequestMapping("index")
public class IndexController {

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("carousel")
    public APIResponse getActiveCarousels() {
        return APIResponse.ok(this.carouselService.getActiveCarousel());
    }

    @GetMapping("cats")
    public APIResponse getCategoriesByType() {
        return APIResponse.ok(this.categoryService.getBaseCategories());
    }

    @GetMapping("subCat/{parentId}")
    public APIResponse getSubCatList(@PathVariable(name = "parentId") Integer parentId) {
        if (parentId == null)
            return APIResponse.errorMsg("Invalid Parent Category ID");

        return APIResponse.ok(this.categoryService.getSubCatListByParentId(parentId));
    }

    @GetMapping("sixNewItems/{rootCatId}")
    public APIResponse getSixNewItems(@PathVariable(name="rootCatId") Integer rootCatId) {
        if (rootCatId == null)
            return APIResponse.errorMsg("Invalid Parent Category ID");

        return APIResponse.ok(this.categoryService.getSixNewItems(rootCatId));
    }
}