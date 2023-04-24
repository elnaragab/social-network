package ru.itgroup.intouch.service;
import lombok.AllArgsConstructor;
import model.post.Post;
import model.post.PostType;
import org.springframework.stereotype.Service;
import ru.itgroup.intouch.mapper.MapperToPostDto;
import ru.itgroup.intouch.repository.PostRepository;
import ru.itgroup.intouch.dto.PostDto;
import ru.itgroup.intouch.repository.PostTypeRepository;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PostService {
    private final TagService tagService;
    private final PostRepository postsRepository;
    private final PostTypeRepository postTypeRepository;
    private final MapperToPostDto mapperToPostDto;
    public PostDto getPostById(Long id) {
        Optional<Post> postEntity = postsRepository.findById(id);
        return postEntity.map(mapperToPostDto::getPostDto).orElse(null);
    }
    public PostDto createNewPost(PostDto postDto) {
        Post post = new Post();
        Optional<PostType> type = postTypeRepository.findByCodeIs(postDto.getType());
        if (type.isEmpty()) {
            return null;
        }
        if (tagService.getTags(postDto.getTags()).isEmpty()) {
            return null;
        }
        post.setTitle(postDto.getTitle());
        post.setDeleted(false);
        post.setPostText(postDto.getPostText());
        post.setCreatedDate(LocalDateTime.now());
        post.setPublishDate(LocalDateTime.now());
        post.setTimeChanged(LocalDateTime.now());
        post.setType(type.get());
        post.setTags(tagService.getTags(postDto.getTags()));
        post.setAuthorId(1); // TODO: integration with AuthorEntity
        post.setImagePath("/random_path/here"); //TODO: integration with storage
        post.setDeleted(false);
        post.setMyLike(false);
        post.setLikeAmount(0);
        postsRepository.save(post);
        return mapperToPostDto.getPostDto(post);
    }
    public boolean deletePostById(Long id) {
        Optional<Post> postEntity = postsRepository.findById(id);
        if (postEntity.isEmpty()) {
            return false;
        }
        postEntity.get().setDeleted(true);
        return true;
    }
}