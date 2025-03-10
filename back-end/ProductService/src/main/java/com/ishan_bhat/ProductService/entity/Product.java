package com.ishan_bhat.ProductService.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;

import javax.persistence.*;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TypeDef(name = "jsonb-node", typeClass = JsonBinaryType.class) // Define jsonb-node type
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "images", columnDefinition = "jsonb") // Keep columnDefinition
    @Type(type = "jsonb-node") // Use @Type, NO @Convert
    private JsonNode images;

    @ManyToOne
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;
}