package com.example.searchtextonwebpages.service;

import com.example.searchtextonwebpages.model.Url;
import com.example.searchtextonwebpages.model.UrlDto;
import com.example.searchtextonwebpages.repository.UrlRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UrlServiceImpl implements UrlService {
    @Autowired
    private UrlRepository urlRepository;

    @Override
    public List<Url> saveAll(List<Url> urls) {
        return urlRepository.saveAll(urls);
    }

    @Override
    public List<Url> findNoneExceptionsUrl(int limit) {
        return urlRepository.findNoneExceptionsUrl(limit);
    }

    @Override
    public int updateStatus(String status, long urlId) {
        return urlRepository.updateStatus(status, urlId);
    }

    @Override
    public List<UrlDto> findAllNotNullStatus() {
        return convertUrlsToDtos(urlRepository.findAllNotNullStatus());
    }

    @Override
    public void deleteAll() {
        urlRepository.deleteAll();
    }

    private List<UrlDto> convertUrlsToDtos(List<Url> urls) {
        List<UrlDto> dtos = new ArrayList<>();
        for (Url url: urls) {
            UrlDto urlDto = new UrlDto();
            urlDto.setStatus(url.getStatus());
            urlDto.setUrl(url.getValue());
            dtos.add(urlDto);
        }
        return dtos;
    }

}
