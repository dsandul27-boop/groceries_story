
package com.example.demo.media;

import org.springframework.web.multipart.MultipartFile;

public interface MediaStorage {

        String storeProductImage(MultipartFile file);


    void deleteProductImage(String imageUrl);
}
