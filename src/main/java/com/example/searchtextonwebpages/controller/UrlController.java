package com.example.searchtextonwebpages.controller;

import com.example.searchtextonwebpages.model.StartDataDto;
import com.example.searchtextonwebpages.model.Url;
import com.example.searchtextonwebpages.model.UrlDto;
import com.example.searchtextonwebpages.service.UrlService;
import com.example.searchtextonwebpages.utils.WebPageUtil;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UrlController {

    @Autowired
    private WebPageUtil webPageUtil;

    @Autowired
    private UrlService urlService;

    @PostMapping("/starting")
    @ResponseBody
    public String find(@RequestBody StartDataDto startDataDto) {
        urlService.deleteAll();
        Set<Url> allUrls = webPageUtil.getAllUrls(startDataDto.getStartUrl(), startDataDto.getMaxCountUrls());
        urlService.saveAll(allUrls);
        return webPageUtil.findText(startDataDto.getSearchText(), startDataDto.getThreadCount());

    }

    @GetMapping("/allUrl")
    public List<UrlDto> getAll() {
        return urlService.findAllNotNullStatus();
    }

}

