
package com.example.demo.service.impl;

import com.example.demo.dto.request.ProductDTORequest;
import com.example.demo.dto.request.ProductFilterCriteria;
import com.example.demo.dto.request.ProductUpdateDTORequest;
import com.example.demo.dto.response.ProductDTOResponse;
import com.example.demo.dto.response.ProductFilterDTOResponse;
import com.example.demo.entities.*;
import com.example.demo.exception.EntityInUseException;
import com.example.demo.exception.IsInactiveException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.NotBlankException;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.media.MediaStorage;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.repository.spec.ProductSpecification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.repository.*;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final VendorRepository vendorRepository;
    private final BatchRepository batchRepository;
    private final ProductTypeRepository productTypeRepository;
    private final UserRepository userRepository;
    private final MediaStorage mediaStorage;

    @Autowired
    public ProductServiceImpl(
            ProductRepository productRepository,
            ProductMapper productMapper,
            VendorRepository vendorRepository,
            BatchRepository batchRepository,
            ProductTypeRepository productTypeRepository,
            UserRepository userRepository,
            MediaStorage mediaStorage) {

        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.vendorRepository = vendorRepository;
        this.batchRepository = batchRepository;
        this.productTypeRepository = productTypeRepository;
        this.userRepository = userRepository;
        this.mediaStorage = mediaStorage;
    }

    private static final Sort DEFAULT_SORT =
            Sort.by("activeStatus").ascending().and(Sort.by("name").ascending());

    @Override
    public ProductDTOResponse save(ProductDTORequest productDTORequest) {
        Optional<Vendor> vendorOptional =
                vendorRepository.findById(productDTORequest.getVendorId());

        if (vendorOptional.isEmpty()) {
            throw new ResourceNotFoundException("Vendor is empty");
        }

        Optional<ProductType> productTypeOptional =
                productTypeRepository.findById(
                        productDTORequest.getProductTypeId());

        if (productTypeOptional.isEmpty()) {
            throw new ResourceNotFoundException("Product type is empty");
        }

        Product product = productMapper.toEntity(productDTORequest);
        product.setVendor(vendorOptional.get());
        product.setProductType(productTypeOptional.get());
        product.setActiveStatus(ActiveStatus.INACTIVE);
        product.setCreatedAt(Instant.now());

        return productMapper.toDTO(productRepository.save(product));
    }

    @Override
    public List<ProductDTOResponse> getAll() {
        List<ProductDTOResponse> productDTOResponseList = new ArrayList<>();
        List<Product> productList = productRepository.findAll();

        if (productList.isEmpty()) {
            throw new ResourceNotFoundException("No one product found");
        }

        for (Product product : productList) {
            productDTOResponseList.add(productMapper.toDTO(product));
        }

        return productDTOResponseList;
    }

    @Override
    public ProductDTOResponse getById(Long productId) {
        Optional<Product> optionalProduct =
                productRepository.findById(productId);

        if (optionalProduct.isEmpty()) {
            throw new ResourceNotFoundException("Product was not found");
        }

        Long productSumQuantity =
                batchRepository.sumQuantityByProductId(productId);

        ProductDTOResponse productDTOResponse =
                productMapper.toDTO(optionalProduct.get());

        productDTOResponse.setQuantity(productSumQuantity);
        return productDTOResponse;
    }

    @Override
    public void hardDelete(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException(
                    "Product already doesn't exist");
        }

        boolean existBatch = batchRepository.existsByProductId(productId);

        if (existBatch) {
            throw new EntityInUseException("Exist batch dependencies");
        }

        productRepository.deleteById(productId);
    }

    @Override
    public void softDelete(Long productId) {
        Optional<Product> optionalProduct =
                productRepository.findById(productId);

        if (optionalProduct.isEmpty()) {
            throw new ResourceNotFoundException("Doesn't exist");
        }

        Product product = optionalProduct.get();

        if (product.getActiveStatus().equals(ActiveStatus.INACTIVE)) {
            throw new IsInactiveException("Product is already inactive");
        }

        product.setActiveStatus(ActiveStatus.INACTIVE);
        productRepository.save(product);
    }

    @Override
    public PageResponse<ProductFilterDTOResponse> getProductFilter(ProductFilterCriteria criteria, Pageable pageable) {
        Pageable effective = pageable.getSort().isSorted()
                ? pageable
                : PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), DEFAULT_SORT);

        Page<Product> page = productRepository.findAll(ProductSpecification.matching(criteria), effective);


        List<Long> ids = page.getContent().stream().map(Product::getId).toList();
        Map<Long, Long> qtyByProduct = ids.isEmpty() ? Map.of()
                : batchRepository.sumActiveQuantityByProductIds(ids).stream()
                .collect(Collectors.toMap(
                        BatchRepository.ProductQuantityView::getProductId, BatchRepository.ProductQuantityView::getQty));

        List<ProductFilterDTOResponse> content = page.getContent().stream()
                .map(p -> productMapper.toFilterDTO(
                        p, qtyByProduct.getOrDefault(p.getId(), 0L)))
                .toList();

        return PageResponse.fromPage(page, content);

    }


    @Override
    public ProductDTOResponse updateProduct(
            Long productId,
            ProductUpdateDTORequest updateDTORequest) {

        Optional<Product> optionalProduct =
                productRepository.findById(productId);

        if (optionalProduct.isEmpty()) {
            throw new ResourceNotFoundException("Product was not found");
        }

        Product product = optionalProduct.get();

        if (updateDTORequest.getName() != null) {
            if (updateDTORequest.getName().isBlank()) {
                throw new NotBlankException("Missing statement");
            }
            product.setName(updateDTORequest.getName());
        }

        if (updateDTORequest.getDescription() != null) {
            if (updateDTORequest.getDescription().isBlank()) {
                throw new NotBlankException("Missing statement");
            }
            product.setDescription(updateDTORequest.getDescription());
        }

        return productMapper.toDTO(productRepository.save(product));
    }


    @Override
    @Transactional
    public ProductDTOResponse uploadProductImage(Long productId, MultipartFile file) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product was not found"));

        String oldImageUrl = product.getImageUrl();
        String newImageUrl = mediaStorage.storeProductImage(file);
        Product savedProduct;

        try {
            product.setImageUrl(newImageUrl);
            savedProduct = productRepository.saveAndFlush(product);
        } catch (RuntimeException exception) {
            mediaStorage.deleteProductImage(newImageUrl);
            throw exception;
        }

        if (oldImageUrl != null && !oldImageUrl.equals(newImageUrl)) {
            deleteImageAfterCommit(oldImageUrl);
        }

        return productMapper.toDTO(savedProduct);
    }

    @Override
    @Transactional
    public ProductDTOResponse deleteProductImage(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product was not found"));

        String oldImageUrl = product.getImageUrl();

        product.setImageUrl(null);
        Product savedProduct = productRepository.save(product);

        if (oldImageUrl != null) {
            deleteImageAfterCommit(oldImageUrl);
        }

        return productMapper.toDTO(savedProduct);
    }


    private void deleteImageAfterCommit(String imageUrl) {
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        try {
                            mediaStorage.deleteProductImage(imageUrl);
                        } catch (RuntimeException ex) {
                            log.warn("Failed to delete old product image {}", imageUrl, ex);
                        }
                    }
                }
        );
    }
}
