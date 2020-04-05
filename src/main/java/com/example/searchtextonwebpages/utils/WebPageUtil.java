package com.example.searchtextonwebpages.utils;

import com.example.searchtextonwebpages.model.Url;
import com.example.searchtextonwebpages.service.UrlService;
import java.io.IOException;
import java.util.ArrayList;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WebPageUtil {
    @Autowired
    private UrlService urlService;

    private List<Url> addUrlsFromPage(Document doc, int maxCountUrls, List<Url> setUrls){
        Elements urls = doc.select("a");
        for (Element tempUrl : urls) {
            if (maxCountUrls == 0) {
                break;
            }
            String tempUrlString = tempUrl.attr("abs:href");
            if (setUrls.stream().map(Url::getValue).noneMatch(v -> v.equals(tempUrlString))
                    && !"".equals(tempUrlString)
                    && (tempUrlString.endsWith(".html") || !tempUrlString.matches(".+\\.[^.]{0,6}$"))
                    && !tempUrlString.contains("sign_in")) {
                Url temUrlEntity = new Url();
                temUrlEntity.setValue(tempUrl.attr("abs:href"));
                setUrls.add(temUrlEntity);
                maxCountUrls--;
            }
        }
        return setUrls;
    }

    private boolean findTextOnPage(String url, String text) throws IOException {
        Element body = Jsoup.connect(url).get().body();
        return body != null && body.text().toLowerCase().contains(text.toLowerCase());
    }

    public List<Url> getAllUrls(String startedUrlString, int maxCountUrls) {
        List<Url> result = new ArrayList<>();
        if (maxCountUrls == 0) {
            return result;
        }
        Url startedUrl = new Url();
        startedUrl.setValue(startedUrlString);
        result.add(startedUrl);
        for (int i = 0; i < result.size(); i++) {
            Url tempUrl = result.get(i);
            String status = null;
            Document doc = null;
            try {
                doc = Jsoup.connect(tempUrl.getValue()).get();
            } catch (Exception e) {
                status = "Error - " + e.getMessage();
            } finally {
                tempUrl.setStatus(status);

            }
            if (doc == null) {
                continue;
            }
            if (result.size() != maxCountUrls) {
                addUrlsFromPage(doc, maxCountUrls - result.size(), result);
            } else {
                break;
            }
        }
        return result;
    }

    public String findText(String searchText, int countThread) {
        List<Url> list = urlService.findNoneExceptionsUrl(countThread);
        while (list.size() != 0) {
            countThread = Math.min(list.size(), countThread);
            Optional<Url> textOnUrls = findTextOnUrls(list.subList(0, countThread), searchText);
            if (textOnUrls.isPresent()) {
                return textOnUrls.get().getValue();
            }
            list = urlService.findNoneExceptionsUrl(countThread);
        }
        return "Not found";
    }

    public Optional<Url> findTextOnUrls(List<Url> list, String searchText) {
        List<Callable<Url>> callableList = getCallableList(list, list.size(), searchText);
        ExecutorService executorService = Executors.newFixedThreadPool(callableList.size());
        List<Future<Url>> futures = null;
        try {
            futures = executorService.invokeAll(callableList);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdown();

        if (futures == null) {
            return Optional.empty();
        }
        for (Future<Url> future : futures) {
            Url futureValue = null;
            try {
                futureValue = future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            if (futureValue != null) {
                return Optional.of(futureValue);
            }
        }
        return Optional.empty();
    }

    private List<Callable<Url>> getCallableList(List<Url> urls , int threadCount, String searchText) {
        List<Callable<Url>> callableTasks = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            Url tempUrl = urls.get(i);
            Callable<Url> callable = () -> {
                tempUrl.setStatus("Downloading");
                urlService.updateStatus(tempUrl.getStatus(), tempUrl.getId());
                boolean textOnPage = false;
                try {
                    textOnPage = findTextOnPage(tempUrl.getValue(), searchText);
                    if (textOnPage) {
                        tempUrl.setStatus("Found");
                    } else {
                        tempUrl.setStatus("Not found");
                    }
                } catch (Exception e) {
                    tempUrl.setStatus(e.getMessage());
                }
                urlService.updateStatus(tempUrl.getStatus(), tempUrl.getId());
                return textOnPage ? tempUrl : null;
            };
            callableTasks.add(callable);
        }
        return callableTasks;
    }
}


