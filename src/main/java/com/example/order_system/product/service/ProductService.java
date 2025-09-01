package com.example.order_system.product.service;

import com.example.order_system.member.domain.Member;
import com.example.order_system.member.repository.MemberRepository;
import com.example.order_system.product.domain.Product;
import com.example.order_system.product.dto.ProductCreateDto;
import com.example.order_system.product.dto.ProductResDto;
import com.example.order_system.product.dto.ProductSearchDto;
import com.example.order_system.product.dto.ProductUpdateDto;
import com.example.order_system.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public Long save(ProductCreateDto productCreateDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("없는 회원입니다."));
        Product product = productRepository.save(productCreateDto.toEntity(member));
        // 1. 프로필 이미지 S3 업로드

        if (productCreateDto.getProductImage() != null && !productCreateDto.getProductImage().isEmpty()) {
            String fileName = "product-" + product.getId() + "-" + System.currentTimeMillis() + "-" + productCreateDto.getProductImage().getOriginalFilename();

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket) // @Value("${cloud.aws.s3.bucket}")로 주입받는 bucket
                    .key(fileName)
                    .contentType(productCreateDto.getProductImage().getContentType())
                    .build();

            // 이미지를 업로드(byte형태로)
            try {
                s3Client.putObject(putObjectRequest, RequestBody.fromBytes(productCreateDto.getProductImage().getBytes()));

            } catch (Exception e) {
                log.error("S3 이미지 업로드 실패: {}", e.getMessage());
                throw new IllegalArgumentException("이미지 업로드 실패");
            }

//            // 이미지 삭제시
//            s3Client.deleteObject(a->a.bucket(버킷명).key(파일명));

            // 이미지 url 추출
            String imgUrl = s3Client.utilities().getUrl(a->a.bucket(bucket).key(fileName)).toExternalForm();

            product.updateImageUrl(imgUrl);
        }

        return product.getId();
    }


    public Page<ProductResDto> findAll(Pageable pageable, ProductSearchDto productSearchDto) {

//
//        return productRepository.findAll().stream()
//                .map(p -> ProductResDto.fromEntity(p)).collect(Collectors.toList());
        Specification<Product> specification = new  Specification<Product>() {

            @Override
            public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                // Root : 엔티티의 속성을 접근하기 위한 객체, CriteriaBuilder : 쿼리를 생성하기 위한 빌더
                List<Predicate> predicateList = new ArrayList<>();
                if(productSearchDto.getCategory() != null){
                    predicateList.add(criteriaBuilder.equal(root.get("category"), productSearchDto.getCategory()));
                }
                if(productSearchDto.getProductName() != null){
                    predicateList.add(criteriaBuilder.like(root.get("name"), "%" + productSearchDto.getProductName() +"%"));
                }
                Predicate[] predicateArr = new Predicate[predicateList.size()]; // 배열생성
                for(int i=0; i<predicateList.size(); i++){
                    predicateArr[i] = predicateList.get(i);
                }

                // 위의 검색조건들을 하나(한줄)의 Predicate 객체로 만들어서 return
                Predicate predicate = criteriaBuilder.and(predicateArr);
                return predicate;
            }
        };
        Page<Product> productList = productRepository.findAll(specification, pageable); // 검색과 페이지 처리
        return productList.map(p->ProductResDto.fromEntity(p)); // 리스트가 아니라서 toList하면 안됨. stream도 안됨

    }


    public ProductResDto findById(Long id) {
        Product product = productRepository.findById(id) // 일단 한번 받기
                .orElseThrow(() -> new EntityNotFoundException("없는 ID입니다."));
        return ProductResDto.fromEntity(product);
    }

// 밑에 내가 해본건 확인해보기..
//    public ProductResDto update(Long id, ProductUpdateDto productUpdateDto) {
//        if(productRepository.findById(id) !=null){
//            Optional<Product> product = productRepository.findById(id);
//            if (product.isPresent()) {
//                String imgUrl = product.get().getImagePath();
//                imgUrl = productUpdateDto.getProductImage().getOriginalFilename();
//
//            }
//            return product.ifPresent(p->{p.getImagePath()= updateDto(id, productUpdateDto).
//
//        }


    public Long update(ProductUpdateDto productUpdateDto, Long productId) { // 이 id는 상품id

        Product product = productRepository.findById(productId).orElseThrow(()-> new EntityNotFoundException("product is not found"));
        product.updateProduct(productUpdateDto);

        if (productUpdateDto.getProductImage() != null && !productUpdateDto.getProductImage().isEmpty()) { // 기존이미지를 대체시키는 방향으로 가면, 이미지가 여러개면 애매해지고 확장성이 떨어져서 안됨
            // 기존이미지를 삭제 : 파일명으로 삭제
            String imgUrl = product.getImagePath();
            String fileName = imgUrl.substring(imgUrl.lastIndexOf("/")+1);
            s3Client.deleteObject(a->a.bucket(bucket).key(fileName));

            // 신규 이미지 등록
            String newFileName = "product-" + product.getId() + "-" + System.currentTimeMillis() + "-" + productUpdateDto.getProductImage().getOriginalFilename();
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(newFileName)
                    .contentType(productUpdateDto.getProductImage().getContentType())
                    .build();

            try {
                s3Client.putObject(putObjectRequest, RequestBody.fromBytes(productUpdateDto.getProductImage().getBytes()));

            } catch (Exception e) {
                log.error("S3 이미지 업로드 실패: {}", e.getMessage());
                throw new IllegalArgumentException("이미지 업로드 실패");
            }

            String newImgUrl = s3Client.utilities().getUrl(a->a.bucket(bucket).key(fileName)).toExternalForm();
            product.updateImageUrl(newImgUrl);
        }else {
            // s3에서 이미지 삭제후 url 갱신
            product.updateImageUrl(null);
        }
        return product.getId();
    }

}




