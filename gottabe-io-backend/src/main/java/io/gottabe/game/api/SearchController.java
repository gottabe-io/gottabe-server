package io.gottabe.game.api;

import io.gottabe.commons.services.PackageDataService;
import io.gottabe.commons.services.PackageGroupService;
import io.gottabe.commons.vo.PackageDataVO;
import io.gottabe.commons.vo.PagedVO;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/search")
public class SearchController {

    private PackageDataService packageDataService;

    @GetMapping
    @Transactional
    public ResponseEntity<List<PackageDataVO>> getStars(@RequestParam("query") String query,
                                                        @RequestParam("page") Integer page,
                                                        @RequestParam("size") Integer size) {
        PagedVO<PackageDataVO> pages = packageDataService.findByQuery(query, page, size);
        return ResponseEntity.ok().header("TOTAL_RESULTS", String.valueOf(pages.getTotalResults())).body(pages.getList());
    }

}
