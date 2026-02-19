package es.codeurjc.scam_g18.service;

import es.codeurjc.scam_g18.model.Tag;
import es.codeurjc.scam_g18.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    public List<Map<String, Object>> getTagsView(List<String> selectedTags) {
        List<Map<String, Object>> tagsView = new ArrayList<>();
        List<Tag> allTags = getAllTags();

        for (Tag tag : allTags) {
            Map<String, Object> tagMap = new HashMap<>();
            tagMap.put("name", tag.getName());
            boolean isActive = selectedTags != null && selectedTags.contains(tag.getName());
            tagMap.put("active", isActive);
            tagsView.add(tagMap);
        }

        return tagsView;
    }
}
