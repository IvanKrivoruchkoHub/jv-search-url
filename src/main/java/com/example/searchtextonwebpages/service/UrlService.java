package com.example.searchtextonwebpages.service;

import com.example.searchtextonwebpages.model.Url;
import com.example.searchtextonwebpages.model.UrlDto;
import java.util.List;
import java.util.Set;

public interface UrlService {
    List<Url> saveAll(Set<Url> urls);

    List<Url> findNoneExceptionsUrl(int limit);

    int updateStatus(String status, long urlId);

    List<UrlDto> findAllNotNullStatus();

    void deleteAll();
}
