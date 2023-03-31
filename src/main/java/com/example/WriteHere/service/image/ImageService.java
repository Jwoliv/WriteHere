package com.example.WriteHere.service.image;


import com.example.WriteHere.model.image.AbstractImage;
import com.example.WriteHere.repository.image.ImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public abstract class ImageService<TI extends AbstractImage, R extends ImageRepository<TI>> {
    public final R repository;


    protected ImageService(R repository) {
        this.repository = repository;
    }

    public TI findById(Long id) {
        return repository.findById(id).orElse(null);
    }
}
